// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
//
// Last modified on Mon 23 February 2009 at  9:58:00 PST by lamport

package tla2sany.semantic;

/***************************************************************************
* A SubstInNode is created to represent the substitutions performed in     *
* module instantiation.  It can appear only as the body of an OpDefNode,   *
* or as the body of a SubstInNode.  This invariant must be maintained      *
* else the handling of expressions that represent subexpressions will      *
* break.                                                                   *
*                                                                          *
* Note: The SubstInNode that represents a substitution A <- expA, B <-     *
* expB with body Bod is at least almost equivalent to an OpApplNode whose  *
* operator is LAMBDA A, B : Bod and whose arguments are expA and expB.     *
* However, for reasons having to do with operators like ENABLED, in which  *
* substitution in definitions isn't equivalent to logical substitution,    *
* we can't handle them the way Lambda expressions are handled, by          *
* creating new OpDef nodes.                                                *
***************************************************************************/
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tla2sany.explorer.ExploreNode;
import tla2sany.explorer.ExplorerVisitor;
import tla2sany.st.TreeNode;
import tla2sany.utilities.Strings;
import tla2sany.utilities.Vector;
import tla2sany.xml.SymbolContext;
import util.UniqueString;

public class SubstInNode extends ExprNode {
  /**
   * For a SubstInNode object s that has the WITH clause
   * <p>
   *    A <- x+1, B <- x*r
   * <p>
   *
   * The substitutions can be accessed as follows:
   *
   *    s.getSubFor(0)  = a ref to the ConstantDecl or VariableDecl
   *                      node for A
   *    s.getSubFor(1)  = a ref to the ConstantDecl or VariableDecl
   *                      node for B
   *    s.getSubWith(0) = a ref to the ExprNode for x+1
   *    s.getSubWith(1) = a ref to the ExprNode for x*r
   */
  private Subst[]           substs;
     // List of explicit and implicit substitutions to be
     // applied to the body.  It should contain substitution
     // for all CONSTANTS and VARIABLES declared in the
     // module being instantiated (whether or not they appear
     // explicitly in the substitution list.
  private ExprNode          body;
     // The expression that the substitutions apply to
  private ModuleNode        instantiatingModule;
     // The module doing the instantiating that resulted in
     //   THIS SubstInNode
  private ModuleNode        instantiatedModule;
     // The module being instantiated

  private SubstInNode(TreeNode treeNode, Subst[] subs, ExprNode expr,
		     ModuleNode ingmn, ModuleNode edmn, Errors errors) {
    super(SubstInKind, treeNode);
    this.substs = subs;
    this.body = expr;
    this.instantiatingModule = ingmn;
    this.instantiatedModule = edmn;
    if (this.body == null) {
      errors.addError(ErrorCode.INTERNAL_ERROR,
		      treeNode.getLocation(), "Substitution error, " +
		      "probably due to error \nin module being instantiated.");
    }
  }

  public SubstInNode(final SubstInNode subst, final ExprNode expr, Errors errors) {
	  this(subst.stn, subst.getSubsts(), expr, subst.getInstantiatingModule(),
				subst.getInstantiatedModule(), errors);
  }
  
  public SubstInNode(final APSubstInNode subst, final ExprNode expr, Errors errors) {
	  this(subst.stn, subst.getSubsts(), expr, subst.getInstantiatingModule(),
				subst.getInstantiatedModule(), errors);
  }
  
  public SubstInNode(TreeNode treeNode, SubstInNode subst, ExprNode expr,
		     ModuleNode ingmn, ModuleNode edmn, Errors errors) {
	  this(treeNode, subst.getSubsts(), expr, ingmn, edmn, errors);
  }

  /**
   * Special constructor for use when an array of default
   * substitutions is to be produced.
   */
  public SubstInNode(TreeNode treeNode, SymbolTable instancerST,
		     Vector<OpDeclNode> instanceeDecls, ModuleNode ingmn, ModuleNode edmn, Errors errors)
  throws AbortException {
    super(SubstInKind, treeNode);
    this.instantiatingModule = ingmn;
    this.instantiatedModule = edmn;
    constructSubst(instanceeDecls, instancerST, treeNode, errors);
    this.body = null;
  }

  public final Subst[] getSubsts() { return this.substs; }

  public final ExprNode getBody() { return this.body; }

  public final void setBody(ExprNode expr) { this.body = expr; }

  public final ModuleNode getInstantiatingModule() {
    return this.instantiatingModule;
  }

  public final ModuleNode getInstantiatedModule()  {
    return this.instantiatedModule;
  }

  /**
   * Returns the OpDeclNode of the ith element of the substitution
   * list.
   */
  public final OpDeclNode getSubFor(int i) {
    return this.substs[i].getOp();
  }

  /**
   * Returns the ExprOrOpArgNode of the ith element of the
   * substitution list.
   */
  public final ExprOrOpArgNode getSubWith(int i) {
    return this.substs[i].getExpr();
  }

  /**
   * For each element of the vector of instanceeDecls of OpDeclNode's,
   * this method puts a default Subst for the same name into
   * "substitutions" if and only if the name can be resolved in the
   * instancerST, i.e.  the SymbolTable of the module doing the
   * instancing.
   *
   * Fill the substitutions array with dummy substitutions, i.e. an
   * OpApplNode or an OpArgNode substituted for each CONSTANT of
   * VARIABLE OpDeclNode in vector v.
   */
  final void constructSubst(Vector<OpDeclNode> instanceeDecls, SymbolTable instancerST,
			    TreeNode treeNode, Errors errors)
  throws AbortException {
    Vector<Subst> vtemp = new Vector<>();

    // for each CONSTANT or VARIABLE declared in module being
    // instantiated (the instancee)
    for ( int i = 0; i < instanceeDecls.size(); i++ ) {
      // Get the OpDeclNode for the CONSTANT or VARIABLE being
      // substituted for, i.e. "c" in" c <- e"
      OpDeclNode decl = instanceeDecls.elementAt(i);

      // Try to resolve the name in the instancer module so we can see
      // if it is recognized as an operator, and if so, what kind of
      // operator it is
      SymbolNode symb = instancerST.resolveSymbol(decl.getName());

      // if the name could be resolved in the instancer module
      // (including parameters created on the LHS of the module
      // instance definition), then create a default substitution for
      // it.  If it cannot be resolved in instancerST, then do
      // nothing, because explicit substitutions have yet to be
      // processed, and then a check for completeness of the
      // substitutions will occur after that.
      if (symb != null){
        // If "decl" is either a VARIABLE declaration, or a CONSTANT
        // declaration for an operator with no arguments, then the
        // expression being substituted must be an ExprNode.  But
        // otherwise (i.e. if it is a CONSTANT declaration for an
        // operator of at least one argument) then the expression being
	// substituted must be an OpArgNode. No other choices are legal.
        if (decl.getKind() == VariableDeclKind ||
	    (decl.getKind() == ConstantDeclKind &&
	     decl.getArity() == 0)) {
	  // Create a new Subst for c <- c, where the c on the RHS is
	  // an OpApplNode with zero arguments
          vtemp.addElement(
             new Subst(decl,
		       new OpApplNode(symb, new ExprOrOpArgNode[0], treeNode, instantiatingModule, errors),
		       null, true));
        }
	else {
	  // Create a new Subst for c <- c, where the c on the RHS is an OpArgNode
          vtemp.addElement(
             new Subst(decl,
		       new OpArgNode(symb, treeNode, instantiatingModule),
		       null, true));
        } // end else
      } // end if
    } // end for

    // The vector vtemp now contains all the default substitutions
    // that are legally possible. Make an array out of them
    this.substs = new Subst[ vtemp.size() ];
    for (int i = 0; i < vtemp.size(); i++) {
      this.substs[i] = (Subst)vtemp.elementAt(i);
    }
  } // end constructSubst()

  /**
   * Add a substitution to the substitutions array, either by
   * overwriting a legal implicit substitution, if one matches, or
   * creating a new one.  In general, the substitutions array on entry
   * to this method can contain a mixture of explicit and implicit
   * substitutions
   */
  final void addExplicitSubstitute(Context instanceCtxt, UniqueString lhs,
                                   TreeNode stn, ExprOrOpArgNode sub, Errors errors) {
    int index;
    for (index = 0; index < this.substs.length; index++) {
      if (lhs == this.substs[index].getOp().getName()) break;
    }

    if (index < this.substs.length) {
      if (!this.substs[index].isImplicit()) {
	// if it is not an implicit substitution, then replacing it is
	// an error.
        errors.addError(
          ErrorCode.INSTANCE_SUBSTITUTION_SYMBOL_REDEFINED_MULTIPLE_TIMES,
          stn.getLocation(),
          "Multiple substitutions for symbol '" + lhs.toString() + "' in substitution."
        );
      }
      else {
	// if it is an implicit subst, then replacing it with an
	// explicit one is fine.
        this.substs[index].setExpr(sub, false);
        this.substs[index].setExprSTN(stn);
      }
    }
    else {
      // but if it is not in the array of implicit substitutions, it
      // is probably because the lhs symbols is not known in the
      // instancer context, which is OK.  But it better be known in
      // the instancee context

      // look up the lhs symbol in the instancee context
      SymbolNode lhsSymbol = instanceCtxt.getSymbol(lhs);

      // lhs must be an OpDeclNode; if not just return, as this error
      // will have been earlier, though semantic analysis was allowed
      // to continue.
      // if the symbol was found, then create a Subst node for it and
      // append it to the substitutions array (which requires a new
      // array allocation and full copy, unfortunately (should fix
      // this at some point)
      // Note if lhsSymbol is null, instanceof returns false.
      if (lhsSymbol instanceof OpDeclNode) {
        int newlength = this.substs.length + 1;
        Subst[] newSubsts = new Subst[ newlength ];
        Subst   newSubst = new Subst((OpDeclNode)lhsSymbol, sub, stn, false);

        System.arraycopy(this.substs, 0, newSubsts, 0, newlength-1);
        newSubsts[newlength-1] = newSubst;

	// replace the old array with the new one
        this.substs = newSubsts;
      }
    }
  }

  /**
   * Make sure there is a substitution for every CONSTANT and VARIABLE
   * of an instantiated module.  If not, try the default, which is
   * that a CONSTANT or VARIABLE X not explicitly substituted for, is
   * implicitly subject to the substitution X <- X.  If that is not
   * possible, because X is not defined in the instantiating module,
   * then we have an error.
   * @param errors Log into which to emit errors.
   */
  final void matchAll(Vector<OpDeclNode> decls, Errors errors) {
    for (int i = 0; i < decls.size(); i++) {
      // Get the name of the i'th operator that must be substituted for
      UniqueString opName = decls.elementAt(i).getName();

      // See if it is represented in the substitutions array
      int j;
      for (j = 0; j < this.substs.length; j++) {
        if (this.substs[j].getOp().getName() == opName) break;
      }

      // If not, then report an error
      if ( j >= this.substs.length ) {
        errors.addError(ErrorCode.INSTANCE_SUBSTITUTION_MISSING_SYMBOL,
			stn.getLocation(),
			"Substitution missing for symbol " + opName + " declared at " +
			decls.elementAt(i).getTreeNode().getLocation() +
			" \nand instantiated in module " + instantiatingModule.getName() + "." );
      }
    }
  }

  /* Level check */
// These nodes are now part of all LevelNode subclasses.
//  private boolean levelCorrect;
//  private int level;
//  private HashSet levelParams;
//  private SetOfLevelConstraints levelConstraints;
//  private SetOfArgLevelConstraints argLevelConstraints;
//  private HashSet argLevelParams;

  @Override
  @SuppressWarnings("unchecked")
  public final boolean levelCheck(int itr, Errors errors) {
    if (this.levelChecked >= itr) return this.levelCorrect;
    this.levelChecked = itr ;

    /***********************************************************************
    * Level check the components body and substs.getSubWith(i) which       *
    * equals substs[i].getExpr().                                          *
    ***********************************************************************/
    this.levelCorrect = true;
    if (!this.body.levelCheck(itr, errors)) {
      this.levelCorrect = false;
    }
    for (int i = 0; i < this.substs.length; i++) {
      if (!this.getSubWith(i).levelCheck(itr, errors)) {
	this.levelCorrect = false;
      }
    }

    // Calculate the level information
    this.level = this.body.getLevel();
    HashSet<SymbolNode> lpSet = this.body.getLevelParams();
    for (int i = 0; i < this.substs.length; i++) {
      if (lpSet.contains(this.getSubFor(i))) {
	this.level = Math.max(level, this.getSubWith(i).getLevel());
      }
    }

//    this.levelParams = new HashSet();
    Iterator<SymbolNode> iter = lpSet.iterator();
    while (iter.hasNext()) {
      this.levelParams.addAll(Subst.paramSet(iter.next(), this.substs));
        /*******************************************************************
        * At this point, levelCheck(itr) has been invoked on              *
        * this.substs[i].getExpr() (which equals this.getSubWith(i)).      *
        *******************************************************************/
    }

    /***********************************************************************
    * The following code was added 22 May 2008 by LL. I had apparently     *
    * forgotten to add the code computing nonLeibnizParams and the         *
    * original code computed only allParams.                               *
    *                                                                      *
    * For Leibniz checking, we go through all the substitutions param <-   *
    * expr.  For each one, if param is in this.body.allParams, then we     *
    * add all parameters expr.allParams to this.allParams.  If param is    *
    * also in this.body.nonLeibnizParams, then we also add all those       *
    * parameters to this.nonLeibnizParams.                                 *
    ***********************************************************************/
// XXXXX Here's the bug.  Need to clone these HashSets, not just
//       set a ref to them.
// Same bug seems to appear in LetInNode with levelParams & allParams
//    (but may not be a bug)
// To check: in APSubstInNode: this.argLevelParams = Subst...
//           in OpDefNode: this.levelParams = EmptySet, ...
//            also, make sure everything set to EmptySet, EmptyLC, EmptyALC
//                  is not changed.

    /***********************************************************************
    * 23 February 2009: Added ".clone" to the following statements to fix  *
    * bug.                                                                 *
    * 23 October 2023: Replaced ".clone" with copy constructor.            *
    ***********************************************************************/
    this.allParams        = new HashSet<>(this.body.getAllParams());
    this.nonLeibnizParams = new HashSet<>(this.body.getNonLeibnizParams());
    for (int i = 0 ; i < this.substs.length ; i++) {
      OpDeclNode param = substs[i].getOp() ;
      if (this.allParams.contains(param)) {
        /*******************************************************************
        * Remove param from this.allParams, add the substituting           *
        * expression's allParams to it, and add the substituting           *
        * expression's nonLeibnizParams to this.nonLeibnizParams.          *
        *******************************************************************/
        this.allParams.remove(param) ;
        this.allParams.addAll(substs[i].getExpr().getAllParams()) ;
        this.nonLeibnizParams.addAll(
              substs[i].getExpr().getNonLeibnizParams()) ;

        /*******************************************************************
        * If param is in this.body.nonLeibnizParams, remove it from        *
        * this.nonLeibnizParams and add the substituting expression's      *
        * allParams to it.                                                 *
        *******************************************************************/
        if (this.nonLeibnizParams.contains(param)) {
          this.nonLeibnizParams.remove(param) ;
          this.nonLeibnizParams.addAll(substs[i].getExpr().getAllParams()) ;

         }; // if
       }; // if (bodyParams.contains(param))
     }; // for


//    /***********************************************************************
//    * For Leibniz checking, we now repeat everything done to compute       *
//    * this.levelParams to compute this.allParams, except using             *
//    * Subst.allParamSet instead of Subst.paramSet.                         *
//    ***********************************************************************/
//    HashSet apSet = this.body.getAllParams();
//    iter = apSet.iterator();
//    while (iter.hasNext()) {
//      Object param = iter.next();
//System.out.println("iter.hasNext() = " + param.toString()) ;
//
//      this.allParams.addAll(Subst.allParamSet(param, this.substs));
//        /*******************************************************************
//        * At this point, levelCheck(itr) has been invoked on               *
//        * this.substs[i].getExpr() (which equals this.getSubWith(i)).      *
//        *******************************************************************/
//      if (this.body.getNonLeibnizParams().contains(param)) {
//        /*******************************************************************
//        * If this param is non-Leibniz in the substitution body, then add  *
//        * the substution's parameters to this.nonLeibnizParams             *
//        *******************************************************************/
//System.out.println("parameter is non-Leibniz") ;
//        this.nonLeibnizParams.addAll(Subst.allParamSet(param, this.substs));
//System.out.println("added: " +
//HashSetToString(Subst.allParamSet(param, this.substs))) ;
//       } // if
//
//    } // while

    boolean isConstant = this.instantiatedModule.isConstant(errors);
      /*********************************************************************
      * It is not necessary to invoke levelCheck before invoking           *
      * isConstant.                                                        *
      *********************************************************************/
    this.levelConstraints = Subst.getSubLCSet(this.body, this.substs,
                                              isConstant, itr, errors);
      /*********************************************************************
      * levelCheck(itr) has been called on body and the                   *
      * substs[i].getExpr(), as required.                                  *
      *********************************************************************/
    this.argLevelConstraints =
        Subst.getSubALCSet(this.body, this.substs, itr, errors);
    this.argLevelParams = Subst.getSubALPSet(this.body, this.substs);
      /*********************************************************************
      * levelCheck(itr) has been called on body and the                   *
      * substs[i].getExpr(), as required.                                  *
      *********************************************************************/
    return this.levelCorrect;
  }

//  public final int getLevel() { return this.level; }
//
//  public final HashSet getLevelParams() { return this.levelParams; }
//
//  public final SetOfLevelConstraints getLevelConstraints() {
//    return this.levelConstraints;
//  }
//
//  public final SetOfArgLevelConstraints getArgLevelConstraints() {
//    return this.argLevelConstraints;
//  }
//
//  public final HashSet getArgLevelParams() {
//    return this.argLevelParams;
//  }

  /**
   * toString, levelDataToString, & walkGraph methods to implement
   * ExploreNode interface
   */
//  public final String levelDataToString() {
//    return "Level: "               + this.level               + "\n" +
//           "LevelParameters: "     + this.levelParams         + "\n" +
//           "LevelConstraints: "    + this.levelConstraints    + "\n" +
//           "ArgLevelConstraints: " + this.argLevelConstraints + "\n" +
//           "ArgLevelParams: "      + this.argLevelParams      + "\n" ;
//  }

  @Override
  public final String toString(int depth, Errors errors) {
    if (depth <= 0) return "";

    String ret = "\n*SubstInNode: "
                 + super.toString(depth, errors)
	         + "\n  instantiating module: " + instantiatingModule.getName()
                 + ", instantiated module: " + instantiatedModule.getName()
                 + Strings.indent(2, "\nSubstitutions:");
    if (this.substs != null) {
      for (int i = 0; i < this.substs.length; i++) {
        ret += Strings.indent(2,
                      Strings.indent(2, "\nSubst:" +
                        (this.substs[i] != null ?
                         Strings.indent(2, this.substs[i].toString(depth-1, errors)) :
                         "<null>")));
      }
    }
    else {
      ret += Strings.indent(2, "<null>");
    }
    ret += Strings.indent(2, "\nBody:"
			  + Strings.indent(2, (body == null ? "<null>" : body.toString(depth-1, errors))));
    return ret;
  }

  /**
   * The children of this node are the body and the expressions
   * being substituted for symbols.
   */
  @Override
  public SemanticNode[] getChildren() {
     SemanticNode[] res = new SemanticNode[this.substs.length + 1];
     res[0] = this.body;
     for (int i = 0; i < substs.length; i++) {
         res[i+1] = substs[i].getExpr();
     }
     return res;
  }

  @Override
  public final void walkGraph(Hashtable<Integer, ExploreNode> semNodesTable, ExplorerVisitor visitor) {
    Integer uid = Integer.valueOf(myUID);
    if (semNodesTable.get(uid) != null) return;

    semNodesTable.put(uid, this);
    visitor.preVisit(this);

    if (this.substs != null) {
      for (int i = 0; i < this.substs.length; i++) {
        if (this.substs[i] != null) this.substs[i].walkGraph(semNodesTable, visitor);
      }
    }
    if (this.body != null) this.body.walkGraph(semNodesTable, visitor);
    visitor.postVisit(this);
    return;
  }

  @Override
  protected Element getLevelElement(Document doc, SymbolContext context) {
      Element sbts = doc.createElement("substs");
      for (int i=0; i<substs.length; i++) {
        sbts.appendChild(substs[i].export(doc,context));
      }
      Element bdy = doc.createElement("body");
      bdy.appendChild(body.export(doc,context));

      Element from = doc.createElement("instFrom");
      Element fromchild = this.instantiatingModule.export(doc, context);
      from.appendChild(fromchild);

      Element to = doc.createElement("instTo");
      Element tochild = instantiatedModule.export(doc,context);
      to.appendChild(tochild);

      Element ret = doc.createElement("SubstInNode");
      ret.appendChild(sbts);
      ret.appendChild(bdy);
      ret.appendChild(from);
      ret.appendChild(to);
      // at the end, we append the context of the symbols used in this node
      //ret.appendChild(instanceeCtxt.export(doc));

      return ret;
    }
}





