package tla2sany;

import tla2sany.modanalyzer.SyntaxTreePrinter;
import tla2sany.parser.SyntaxTreeNode;
import tla2sany.parser.TLAplusParser;
import tla2sany.parser.TLAplusParserConstants;
import tla2sany.st.SyntaxTreeConstants;
import tla2sany.st.TreeNode;

import java.io.PrintWriter;
import java.lang.Character;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AstDsl {	
	/**
	 * Enumeration of all node kinds in the AST DSL. This list was generated
	 * by running the following short Python3 script against node-types.json
	 * in the tree-sitter-tlaplus repository:
	 * 
	 * import json
	 * f = open('src/node-types.json', 'r', encoding='utf-8')
	 * data = json.load(f)
	 * f.close()
	 * names = [
     *   f'\t\t{item["type"].upper()} ("{item["type"]}")'
     *   for item in data
     *   if item['named'] and not item['type'].startswith('_')
	 * ]
	 * print(',\n'.join(names) + ';\n')
	 */
	public static enum AstNodeKind {
		ADDRESS ("address"),
		ALL_MAP_TO ("all_map_to"),
		ALWAYS ("always"),
		APPROX ("approx"),
		ASSIGN ("assign"),
		ASSUME_PROVE ("assume_prove"),
		ASSUMPTION ("assumption"),
		ASYMP ("asymp"),
		BIGCIRC ("bigcirc"),
		BINARY_NUMBER ("binary_number"),
		BLOCK_COMMENT ("block_comment"),
		BLOCK_COMMENT_TEXT ("block_comment_text"),
		BNF_RULE ("bnf_rule"),
		BOOLEAN ("boolean"),
		BOUND_INFIX_OP ("bound_infix_op"),
		BOUND_NONFIX_OP ("bound_nonfix_op"),
		BOUND_OP ("bound_op"),
		BOUND_POSTFIX_OP ("bound_postfix_op"),
		BOUND_PREFIX_OP ("bound_prefix_op"),
		BOUNDED_QUANTIFICATION ("bounded_quantification"),
		BULLET ("bullet"),
		BULLET_CONJ ("bullet_conj"),
		BULLET_DISJ ("bullet_disj"),
		CAP ("cap"),
		CASE ("case"),
		CASE_ARM ("case_arm"),
		CASE_ARROW ("case_arrow"),
		CASE_BOX ("case_box"),
		CASE_PROOF_STEP ("case_proof_step"),
		CDOT ("cdot"),
		CHILD_ID ("child_id"),
		CHOOSE ("choose"),
		CIRC ("circ"),
		COLON ("colon"),
		CONG ("cong"),
		CONJ_ITEM ("conj_item"),
		CONJ_LIST ("conj_list"),
		CONSTANT_DECLARATION ("constant_declaration"),
		CUP ("cup"),
		DEF_EQ ("def_eq"),
		DEFINITION_PROOF_STEP ("definition_proof_step"),
		DISJ_ITEM ("disj_item"),
		DISJ_LIST ("disj_list"),
		DIV ("div"),
		DOMAIN ("domain"),
		DOTEQ ("doteq"),
		DOTS_2 ("dots_2"),
		DOTS_3 ("dots_3"),
		DOUBLE_LINE ("double_line"),
		ENABLED ("enabled"),
		EQ ("eq"),
		EQUIV ("equiv"),
		ESCAPE_CHAR ("escape_char"),
		EVENTUALLY ("eventually"),
		EXCEPT ("except"),
		EXCEPT_UPDATE ("except_update"),
		EXCEPT_UPDATE_FN_APPL ("except_update_fn_appl"),
		EXCEPT_UPDATE_RECORD_FIELD ("except_update_record_field"),
		EXCEPT_UPDATE_SPECIFIER ("except_update_specifier"),
		EXCL ("excl"),
		EXISTS ("exists"),
		EXTENDS ("extends"),
		FAIR ("fair"),
		FAIRNESS ("fairness"),
		FINITE_SET_LITERAL ("finite_set_literal"),
		FORALL ("forall"),
		FUNCTION_DEFINITION ("function_definition"),
		FUNCTION_EVALUATION ("function_evaluation"),
		FUNCTION_LITERAL ("function_literal"),
		GEQ ("geq"),
		GETS ("gets"),
		GG ("gg"),
		HAVE_PROOF_STEP ("have_proof_step"),
		HEADER_LINE ("header_line"),
		HEX_NUMBER ("hex_number"),
		IF_THEN_ELSE ("if_then_else"),
		IFF ("iff"),
		IMPLIES ("implies"),
		IN ("in"),
		INFIX_OP_SYMBOL ("infix_op_symbol"),
		INNER_ASSUME_PROVE ("inner_assume_prove"),
		INSTANCE ("instance"),
		INT_NUMBER_SET ("int_number_set"),
		LABEL ("label"),
		LABEL_AS ("label_as"),
		LAMBDA ("lambda"),
		LAND ("land"),
		LANGLE_BRACKET ("langle_bracket"),
		LD_TTILE ("ld_ttile"),
		LEADS_TO ("leads_to"),
		LEQ ("leq"),
		LET_IN ("let_in"),
		LEVEL ("level"),
		LL ("ll"),
		LNOT ("lnot"),
		LOCAL_DEFINITION ("local_definition"),
		LOR ("lor"),
		LS_TTILE ("ls_ttile"),
		LT ("lt"),
		MAPS_TO ("maps_to"),
		MINUS ("minus"),
		MODULE ("module"),
		MODULE_DEFINITION ("module_definition"),
		MODULE_REF ("module_ref"),
		NAT_NUMBER ("nat_number"),
		NAT_NUMBER_SET ("nat_number_set"),
		NEGATIVE ("negative"),
		NEQ ("neq"),
		NEW ("new"),
		NON_TERMINAL_PROOF ("non_terminal_proof"),
		NOTIN ("notin"),
		OCTAL_NUMBER ("octal_number"),
		ODOT ("odot"),
		OMINUS ("ominus"),
		OPERATOR_ARGS ("operator_args"),
		OPERATOR_DECLARATION ("operator_declaration"),
		OPERATOR_DEFINITION ("operator_definition"),
		OPLUS ("oplus"),
		OSLASH ("oslash"),
		OTHER_ARM ("other_arm"),
		OTIMES ("otimes"),
		PARENTHESES ("parentheses"),
		PCAL_ALGORITHM ("pcal_algorithm"),
		PCAL_ALGORITHM_BODY ("pcal_algorithm_body"),
		PCAL_ALGORITHM_START ("pcal_algorithm_start"),
		PCAL_ASSERT ("pcal_assert"),
		PCAL_ASSIGN ("pcal_assign"),
		PCAL_AWAIT ("pcal_await"),
		PCAL_DEFINITIONS ("pcal_definitions"),
		PCAL_EITHER ("pcal_either"),
		PCAL_GOTO ("pcal_goto"),
		PCAL_IF ("pcal_if"),
		PCAL_LHS ("pcal_lhs"),
		PCAL_MACRO ("pcal_macro"),
		PCAL_MACRO_CALL ("pcal_macro_call"),
		PCAL_MACRO_DECL ("pcal_macro_decl"),
		PCAL_PRINT ("pcal_print"),
		PCAL_PROC_CALL ("pcal_proc_call"),
		PCAL_PROC_DECL ("pcal_proc_decl"),
		PCAL_PROC_VAR_DECL ("pcal_proc_var_decl"),
		PCAL_PROC_VAR_DECLS ("pcal_proc_var_decls"),
		PCAL_PROCEDURE ("pcal_procedure"),
		PCAL_PROCESS ("pcal_process"),
		PCAL_RETURN ("pcal_return"),
		PCAL_SKIP ("pcal_skip"),
		PCAL_VAR_DECL ("pcal_var_decl"),
		PCAL_VAR_DECLS ("pcal_var_decls"),
		PCAL_WHILE ("pcal_while"),
		PCAL_WITH ("pcal_with"),
		PICK_PROOF_STEP ("pick_proof_step"),
		PLUS ("plus"),
		PLUS_ARROW ("plus_arrow"),
		POSTFIX_OP_SYMBOL ("postfix_op_symbol"),
		POWERSET ("powerset"),
		PREC ("prec"),
		PRECEQ ("preceq"),
		PREFIX_OP_SYMBOL ("prefix_op_symbol"),
		PREFIXED_OP ("prefixed_op"),
		PREV_FUNC_VAL ("prev_func_val"),
		PROOF_STEP ("proof_step"),
		PROOF_STEP_ID ("proof_step_id"),
		PROOF_STEP_REF ("proof_step_ref"),
		PROPTO ("propto"),
		QED_STEP ("qed_step"),
		QQ ("qq"),
		QUANTIFIER_BOUND ("quantifier_bound"),
		RANGLE_BRACKET ("rangle_bracket"),
		RANGLE_BRACKET_SUB ("rangle_bracket_sub"),
		RD_TTILE ("rd_ttile"),
		REAL_NUMBER_SET ("real_number_set"),
		RECORD_LITERAL ("record_literal"),
		RECORD_VALUE ("record_value"),
		RECURSIVE_DECLARATION ("recursive_declaration"),
		RS_TTILE ("rs_ttile"),
		SET_FILTER ("set_filter"),
		SET_IN ("set_in"),
		SET_MAP ("set_map"),
		SET_OF_FUNCTIONS ("set_of_functions"),
		SET_OF_RECORDS ("set_of_records"),
		SIM ("sim"),
		SIMEQ ("simeq"),
		SINGLE_LINE ("single_line"),
		SOURCE_FILE ("source_file"),
		SQCAP ("sqcap"),
		SQCUP ("sqcup"),
		SQSUBSET ("sqsubset"),
		SQSUBSETEQ ("sqsubseteq"),
		SQSUPSET ("sqsupset"),
		SQSUPSETEQ ("sqsupseteq"),
		STAR ("star"),
		STEP_EXPR_NO_STUTTER ("step_expr_no_stutter"),
		STEP_EXPR_OR_STUTTER ("step_expr_or_stutter"),
		STRING ("string"),
		SUBEXPR_COMPONENT ("subexpr_component"),
		SUBEXPR_PREFIX ("subexpr_prefix"),
		SUBEXPR_TREE_NAV ("subexpr_tree_nav"),
		SUBEXPRESSION ("subexpression"),
		SUBSET ("subset"),
		SUBSETEQ ("subseteq"),
		SUBSTITUTION ("substitution"),
		SUCC ("succ"),
		SUCCEQ ("succeq"),
		SUFFICES_PROOF_STEP ("suffices_proof_step"),
		SUP_PLUS ("sup_plus"),
		SUPSET ("supset"),
		SUPSETEQ ("supseteq"),
		TAKE_PROOF_STEP ("take_proof_step"),
		TEMPORAL_EXISTS ("temporal_exists"),
		TEMPORAL_FORALL ("temporal_forall"),
		TERMINAL_PROOF ("terminal_proof"),
		THEOREM ("theorem"),
		TIMES ("times"),
		TUPLE_LITERAL ("tuple_literal"),
		TUPLE_OF_IDENTIFIERS ("tuple_of_identifiers"),
		UNBOUNDED_QUANTIFICATION ("unbounded_quantification"),
		UNCHANGED ("unchanged"),
		UNION ("union"),
		UPLUS ("uplus"),
		USE_BODY ("use_body"),
		USE_BODY_DEF ("use_body_def"),
		USE_BODY_EXPR ("use_body_expr"),
		USE_OR_HIDE ("use_or_hide"),
		VARIABLE_DECLARATION ("variable_declaration"),
		VERTVERT ("vertvert"),
		WITNESS_PROOF_STEP ("witness_proof_step"),
		WR ("wr"),
		AMP ("amp"),
		AMPAMP ("ampamp"),
		ASTERISK ("asterisk"),
		BOOLEAN_SET ("boolean_set"),
		COMMENT ("comment"),
		COMPOSE ("compose"),
		DOL ("dol"),
		DOLDOL ("doldol"),
		EXTRAMODULAR_TEXT ("extramodular_text"),
		FORMAT ("format"),
		GT ("gt"),
		HASHHASH ("hashhash"),
		IDENTIFIER ("identifier"),
		IDENTIFIER_REF ("identifier_ref"),
		MAP_FROM ("map_from"),
		MAP_TO ("map_to"),
		MINUSMINUS ("minusminus"),
		MOD ("mod"),
		MODMOD ("modmod"),
		MUL ("mul"),
		MULMUL ("mulmul"),
		NAME ("name"),
		PCAL_END_EITHER ("pcal_end_either"),
		PCAL_END_IF ("pcal_end_if"),
		PCAL_END_WHILE ("pcal_end_while"),
		PCAL_END_WITH ("pcal_end_with"),
		PLACEHOLDER ("placeholder"),
		PLUSPLUS ("plusplus"),
		POW ("pow"),
		POWPOW ("powpow"),
		PRIME ("prime"),
		REAL_NUMBER ("real_number"),
		SETMINUS ("setminus"),
		SLASH ("slash"),
		SLASHSLASH ("slashslash"),
		STRING_SET ("string_set"),
		SUP_HASH ("sup_hash"),
		VALUE ("value"),
		VERT ("vert");

		/**
		 * The node kind name as found in the unparsed AST.
		 */
		public final String name;
		
		/**
		 * A static map from node kind name to enum to avoid linear lookups.
		 */
		private static final Map<String, AstNodeKind> nameMap = new HashMap<String, AstNodeKind>();
		
		/**
		 * Tracks node kinds that go unused; useful for identifying gaps in testing.
		 */
		private static final Set<AstNodeKind> unused = new HashSet<AstNodeKind>();
		
		/**
		 * Initialize the static maps on class load.
		 */
		static {
			for (AstNodeKind k : AstNodeKind.values()) {
				AstNodeKind.nameMap.put(k.name, k);
			}
			
			AstNodeKind.unused.addAll(AstNodeKind.nameMap.values());
		}
		
		/**
		 * Private constructor for the enum so it can have a string field.
		 * 
		 * @param name The node kind name as found in the unparsed AST.
		 */
		private AstNodeKind(String name) {
			this.name = name;
		}
		
		/**
		 * Get list of unused AST node kinds; useful for identifying gaps in testing.
		 * 
		 * @return The list of AST node kinds that were never constructed during parsing.
		 */
		public static List<AstNodeKind> getUnused() {
			return new ArrayList<AstNodeKind>(AstNodeKind.unused);
		}
		
		/**
		 * Gets the AST node kind enum from the unparsed AST string.
		 * 
		 * @param text The node kind name from the unparsed AST string.
		 * @return An enum corresponding to the given node kind name.
		 * @throws ParseException If no such node kind name is found.
		 */
		public static AstNodeKind fromString(String text) throws ParseException {
			if (AstNodeKind.nameMap.containsKey(text)) {
				AstNodeKind kind = AstNodeKind.nameMap.get(text);
				AstNodeKind.unused.remove(kind);
				return kind;
			} else {
				throw new ParseException(String.format("Could not find node %s", text), 0);
			}
		}
		
		public AstNode asNode() {
			return new AstNode(this);
		}
	}
	
	/**
	 * An abstract syntax tree (AST) node defining a parse tree.
	 */
	public static class AstNode {
		/**
		 * The kind of the AST node.
		 */
		public final AstNodeKind kind;
		
		/**
		 * List of both named & unnamed children, in order of appearance.
		 */
		public final List<AstNode> children;
		
		/**
		 * List of named children in order of appearance.
		 */
		public final Map<String, AstNode> fields;
		
		/**
		 * Index to quickly check whether a child is named or not, and get its
		 * field name if so.
		 */
		private final Map<AstNode, String> fieldNames;

		/**
		 * Constructs an AST node.
		 * 
		 * @param kind The kind of the AST node.
		 * @param children The children of the AST node.
		 * @param fields The named field children of the AST node.
		 */
		public AstNode(AstNodeKind kind, List<AstNode> children, Map<String, AstNode> fields) {
			this.kind = kind;
			this.children = children;
			this.fields = fields;
			HashMap<AstNode, String> fieldNames = new HashMap<AstNode, String>();
			for (Map.Entry<String, AstNode> entry : this.fields.entrySet()) {
				fieldNames.put(entry.getValue(), entry.getKey());
			}
			this.fieldNames = fieldNames;
		}
		
		public AstNode(AstNodeKind kind, List<AstNode> children) {
			this(kind, children, new HashMap<String, AstNode>());
		}
		
		public AstNode(AstNodeKind kind) {
			this(kind, new ArrayList<AstNode>());
		}
		
		public void addChild(AstNode child) {
			this.children.add(child);
		}
		
		public void addChildren(List<AstNode> children) {
			this.children.addAll(children);
		}
		
		public void addField(String name, AstNode child) {
			this.children.add(child);
			this.fields.put(name, child);
			this.fieldNames.put(child, name);
		}
		
		/**
		 * Builds a string representation of the subtree using a StringBuilder.
		 * 
		 * @param sb The StringBuilder onto which to append output.
		 */
		private void toString(StringBuilder sb) {
			sb.append('(');
			sb.append(this.kind.name);
			if (!this.children.isEmpty()) {
				sb.append(' ');
			}
			for (int i = 0; i < this.children.size(); i++) {
				AstNode child = this.children.get(i);
				if (this.fieldNames.containsKey(child)) {
					sb.append(this.fieldNames.get(child));
					sb.append(": ");
					child.toString(sb);
				} else {
					child.toString(sb);
				}
				if (i < this.children.size() - 1) {
					sb.append(' ');
				}
			}
			sb.append(')');
		}
		
		/**
		 * Dumps the AST node subtree to a string. Useful for debugging. The
		 * inverse of the parse function.
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder();
			this.toString(sb);
			return sb.toString();
		}
	}
	
	/**
	 * Mutable class tracking parser state to avoid string copies.
	 */
	private static class ParserState {
		/**
		 * The parser's current index in the string.
		 */
		public int i;
		
		/**
		 * The unparsed input string.
		 */
		public final String input;
		
		/**
		 * Whether to log each character consumed.
		 */
		private final boolean log;
		
		/**
		 * Initializes the parser state with the input string.
		 * 
		 * @param input The string to parse.
		 * @param log Whether to log each consumed char.
		 */
		public ParserState(String input, boolean log) {
			this.i = 0;
			this.input = input;
			this.log = log;
		}
		
		/**
		 * Whether the input has been fully consumed.
		 * 
		 * @return True if there is no input remaining.
		 */
		public boolean isEof() {
			return i >= input.length();
		}

		/**
		 * Consumes input until encountering a non-whitespace char.
		 */
		public void consumeWhitespace() {
			while (!this.isEof() && Character.isWhitespace(input.charAt(i))) {
				this.consume();
			}
		}
		
		/**
		 * Whether the current char is the given char.
		 * 
		 * @param c The char to check.
		 * @return True if not EOF and char matches.
		 */
		public boolean matches(char c) {
			return !this.isEof() && input.charAt(i) == c;
		}
		
		/**
		 * Consumes the current char without any checks. Possibly logs this act.
		 * 
		 * @return The consumed char.
		 */
		private char consume() {
			char c = input.charAt(i);
			if (this.log) {
				System.out.println(c);
			}
			i++;
			return c;
		}
		
		/**
		 * Consume the current char if it is as expected.
		 * 
		 * @param c The expected char.
		 * @return Whether the current char was consumed.
		 */
		public boolean consumeIf(char c) {
			if (this.matches(c)) {
				this.consume();
				return true;
			} else {
				return false;
			}
		}
		
		/**
		 * Whether the current char is an identifier char.
		 * 
		 * @return True if it is an identifier char.
		 */
		public boolean isIdentifierChar() {
			return !this.isEof() && (Character.isLetter(input.charAt(i)) || input.charAt(i) == '_');
		}
		
		/**
		 * Consumes the upcoming identifier then returns it as a string.
		 * 
		 * @return The consumed identifier.
		 */
		public String consumeIdentifier() {
			StringBuilder sb = new StringBuilder();
			while (this.isIdentifierChar()) {
				sb.append(this.consume());
			}
			
			return sb.toString();
		}
		
		/**
		 * Constructs an appropriate parse exception for the current position.
		 * 
		 * @param expected The char or element that was expected instead.
		 * @return A relatively informative parse exception to throw.
		 */
		public ParseException error(String expected) {
			if (this.isEof()) {
				return new ParseException(String.format("Unexpected EOF; expected %s: %s", expected, input), i);
			} else {
				return new ParseException(String.format("Unexpected char %c; expected %s at index %d: %s", input.charAt(i), expected, i, input), i);
			}
		}
	}
	
	/**
	 * Performs the actual parsing of the AST DSL by mutating a parser state
	 * in place. Thankfully S-expressions are easy to parse in a single
	 * recursive function.
	 * 
	 * @param s Parser state holding the input string and current index.
	 * @return Root node of the AST subtree at this point in the input.
	 * @throws ParseException If invalid S-expression syntax is encountered.
	 */
	private static AstNode parseAst(ParserState s) throws ParseException {
		s.consumeWhitespace();
		// Looking for ex. (source_file ...)
		if (s.consumeIf('(')) {
			s.consumeWhitespace();
			if (!s.isIdentifierChar()) {
				throw s.error("Identifier Char");
			}
			AstNodeKind kind = AstNodeKind.fromString(s.consumeIdentifier());
			List<AstNode> children = new ArrayList<AstNode>();
			Map<String, AstNode> fields = new HashMap<String, AstNode>();
			s.consumeWhitespace();
			while (!s.isEof() && !s.matches(')')) {
				if (s.matches('(')) {
					// Found unnamed child ex. (source_file (module ... ))
					children.add(parseAst(s));
				} else if (s.isIdentifierChar()) {
					// Found named field child ex. (bound_op name: (...) ...)
					String fieldName = s.consumeIdentifier();
					s.consumeWhitespace();
					if (s.consumeIf(':')) {
						s.consumeWhitespace();
						if (s.matches('(')) {
							// Recurse to get actual named child
							AstNode namedChild = parseAst(s);
							fields.put(fieldName, namedChild);
							children.add(namedChild);
						} else {
							throw s.error("(");
						}
					} else {
						throw s.error(":");
					}
				} else {
					throw s.error("(");
				}
				s.consumeWhitespace();
			}
			
			if (!s.consumeIf(')')) {
				throw s.error(")");
			}
			
			return new AstNode(kind, children, fields);
		} else {
			throw s.error("(");
		}
	}
	
	/**
	 * Parse the given S-expression string into an abstract syntax tree.
	 * 
	 * @param input The S-expression in string form.
	 * @return Root node of the AST.
	 * @throws ParseException If invalid S-expression syntax is encountered.
	 */
	public static AstNode parseAst(String input) throws ParseException {
		return parseAst(new ParserState(input, false));
	}
	
	private static void printSanyParseTree(TLAplusParser sanyTree) {
		PrintWriter out = new PrintWriter(System.out);
		SyntaxTreePrinter.print(sanyTree, out);
		out.flush();
	}
	
	private static AstNode optional(SyntaxTreeNode node) throws ParseException {
		if (null != node && null != node.zero() && 0 != node.zero().length) {
			return treeNodeToAstNode(node);
		} else {
			return null;
		}
	}
	
	private static List<AstNode> repeat(SyntaxTreeNode node) throws ParseException {
		List<AstNode> children = new ArrayList<AstNode>();
		for (SyntaxTreeNode child : node.getHeirs()) {
			children.add(treeNodeToAstNode(child));
		}
		
		return children;
	}
	
	public static AstNode opFromString(String op) throws ParseException {
		switch (op) {
			case "SUBSET":
				return AstNodeKind.SUBSET.asNode();
			default:
				throw new ParseException(String.format("Operator translation not defined: %s", op), 0);
		}
	}
	
	/**
	 * A somewhat monstrous function which translates the parse tree emitted
	 * by SANY to the parse tree defined by the AST DSL. This function has a
	 * lot of cases but the translation process is very mechanical.
	 * 
	 * In the event that a new test is added for a node kind which does not
	 * yet have a defined translation, the thrown ParseException should
	 * contain info on the kind ID. Search this kind ID in either SyntaxTreeConstants
	 * or TLAplusParserConstants, add a new case to the top-level switch
	 * statement for the kind ID, then set a debug breakpoint in that case.
	 * Run the test in the debugger to look at the object emitted by SANY;
	 * the node children will be in some order in the heirs array. In this
	 * fashion you can define the necessary translation to the AST DSL by
	 * copying the approach taken in other switch branches. Don't be
	 * intimidated! It is easier than it looks. It helps to add assert
	 * statements as documentation of what kinds SANY emits in each heir.
	 * 
	 * @param node The SANY syntax node to translate to the AST DSL node.
	 * @return An AST DSL node corresponding to the given SANY node.
	 * @throws ParseException If a translation is not yet defined for the node.
	 */
	public static AstNode treeNodeToAstNode(SyntaxTreeNode node) throws ParseException {
		SyntaxTreeNode[] heirs = node.getHeirs();
		switch (node.getKind()) {
			case SyntaxTreeConstants.N_Module: {
				AstNode sourceFile = AstNodeKind.SOURCE_FILE.asNode();
				AstNode module = AstNodeKind.MODULE.asNode();
				sourceFile.addChild(module);
				module.addChild(AstNodeKind.HEADER_LINE.asNode()); // heirs[0].heirs[0]
				module.addField("name", AstNodeKind.IDENTIFIER.asNode()); // heirs[0].heirs[1]
				module.addChild(AstNodeKind.HEADER_LINE.asNode()); // heirs[0].heirs[2]
				module.addChildren(repeat(heirs[1])); // EXTENDS statements
				module.addChildren(repeat(heirs[2])); // unit definitions
				module.addChild(AstNodeKind.DOUBLE_LINE.asNode()); // heirs[3]
				return sourceFile;
			} case SyntaxTreeConstants.N_OperatorDefinition: {
				AstNode operatorDefinition = AstNodeKind.OPERATOR_DEFINITION.asNode();
				operatorDefinition.addField("name", treeNodeToAstNode(heirs[0]));
				operatorDefinition.addChild(AstNodeKind.DEF_EQ.asNode()); // heirs[1]
				operatorDefinition.addField("definition", treeNodeToAstNode(heirs[2]));
				return operatorDefinition;
			} case SyntaxTreeConstants.N_PrefixExpr: {
				AstNode boundPrefixOp = AstNodeKind.BOUND_PREFIX_OP.asNode();
				boundPrefixOp.addField("symbol", treeNodeToAstNode(heirs[0]));
				boundPrefixOp.addField("rhs", treeNodeToAstNode(heirs[1]));
				return boundPrefixOp;
			} case SyntaxTreeConstants.N_IdentLHS: {
				return AstNodeKind.IDENTIFIER.asNode();
			} case SyntaxTreeConstants.N_ConjList: {
				AstNode conjList = AstNodeKind.CONJ_LIST.asNode();
				for (SyntaxTreeNode child : heirs) {
					conjList.addChild(treeNodeToAstNode(child));
				}
				return conjList;
			} case SyntaxTreeConstants.N_ConjItem: {
				AstNode conjItem = AstNodeKind.CONJ_ITEM.asNode();
				assert(heirs[0].isKind(TLAplusParserConstants.AND));
				conjItem.addChild(AstNodeKind.BULLET_CONJ.asNode());
				conjItem.addChild(treeNodeToAstNode(heirs[1])); // expr
				return conjItem;
			} case SyntaxTreeConstants.N_DisjList: {
				AstNode disjList = AstNodeKind.DISJ_LIST.asNode();
				for (SyntaxTreeNode child : heirs) {
					disjList.addChild(treeNodeToAstNode(child));
				}
				return disjList;
			} case SyntaxTreeConstants.N_DisjItem: {
				AstNode disjItem = AstNodeKind.DISJ_ITEM.asNode();
				assert(heirs[0].isKind(TLAplusParserConstants.OR));
				disjItem.addChild(AstNodeKind.BULLET_DISJ.asNode());
				disjItem.addChild(treeNodeToAstNode(heirs[1])); // expr
				return disjItem;
			} case SyntaxTreeConstants.N_GeneralId: {
				assert(heirs[0].isKind(SyntaxTreeConstants.N_IdPrefix));
				assert(heirs[1].isKind(TLAplusParserConstants.IDENTIFIER));
				return AstNodeKind.IDENTIFIER_REF.asNode();
			} case SyntaxTreeConstants.N_Number: {
				// TODO: parse & classify number
				return AstNodeKind.NAT_NUMBER.asNode();
			} case SyntaxTreeConstants.N_String: {
				return AstNodeKind.STRING.asNode();
			} case SyntaxTreeConstants.N_Tuple: {
				AstNode tuple = AstNodeKind.TUPLE_LITERAL.asNode();
				assert(heirs[0].isKind(TLAplusParserConstants.LAB));
				tuple.addChild(AstNodeKind.LANGLE_BRACKET.asNode());
				int i = 1;
				while (i < heirs.length - 1) {
					tuple.addChild(treeNodeToAstNode(heirs[i]));
					i++;
					if (i < heirs.length - 2) {
						assert(heirs[i].isKind(TLAplusParserConstants.COMMA));
						i++;
					}
				}
				assert(heirs[heirs.length-1].isKind(TLAplusParserConstants.RAB));
				tuple.addChild(AstNodeKind.RANGLE_BRACKET.asNode());
				return tuple;
			} case SyntaxTreeConstants.N_ActionExpr: {
				boolean allowStutter = heirs[0].isKind(TLAplusParserConstants.LSB);
				AstNode actionExpr = allowStutter ? AstNodeKind.STEP_EXPR_OR_STUTTER.asNode() : AstNodeKind.STEP_EXPR_NO_STUTTER.asNode();
				assert(heirs[0].isKind(allowStutter ? TLAplusParserConstants.LSB : TLAplusParserConstants.LAB));
				actionExpr.addChild(treeNodeToAstNode(heirs[1])); // expr
				assert(heirs[2].isKind(allowStutter ? TLAplusParserConstants.ARSB : TLAplusParserConstants.ARAB));
				actionExpr.addChild(treeNodeToAstNode(heirs[3])); // subscript expr
				return actionExpr;
			} case SyntaxTreeConstants.N_InfixExpr: {
				AstNode boundInfixOp = AstNodeKind.BOUND_INFIX_OP.asNode();
				return boundInfixOp;
			} case SyntaxTreeConstants.N_GenPrefixOp: {
				assert(heirs[0].isKind(SyntaxTreeConstants.N_IdPrefix));
				return treeNodeToAstNode(heirs[1]); // N_PrefixOp
			} case SyntaxTreeConstants.N_PrefixOp: {
				return opFromString(node.image.toString());
			} default: {
				throw new ParseException(String.format("Unhandled conversion from kind %d image %s", node.getKind(), node.getImage()), 0);
			}
		}
	}
	
	/**
	 * Whether the expected AST matches the actual AST from SANY.
	 * 
	 * @param expected The expected AST.
	 * @param actual The actual AST generated by SANY.
	 * @return True if match was successful.
	 */
	public static boolean matchesExpectedAst(AstNode expected, TLAplusParser sanyActual) throws ParseException {
		System.out.println(expected.toString());
		AstNode actual = treeNodeToAstNode(sanyActual.ParseTree);
		System.out.println(actual.toString());
		return true;
	}
}
