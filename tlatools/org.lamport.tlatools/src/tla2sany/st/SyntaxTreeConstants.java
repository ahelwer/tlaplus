// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// jcg wrote this.
// Last modified on Wed 15 Aug 2007 at 15:45:47 PST by lamport
// last revised February 1st 2000 by J-Ch


// List of all kinds of nodes one can find in the syntax tree,
// together with a method to generate a representation

// numbering starts at 227, previous numbers will identify tokens (with an identifier 
// generated automatically by javacc

/***************************************************************************
* The value assigned to NULL_ID must be greater than the highest token     *
* number generated by javacc in parser/TLAplusParserConstants.java.  As    *
* of 20 Apr 2007, the highest token number was 226 and NULL_ID was set     *
* equal to 227.  So, I upped the numbers by 100.                           *
***************************************************************************/
package tla2sany.st;

import util.UniqueString;

public interface SyntaxTreeConstants {

  int  NULL_ID =  327;
  int  N_ActDecl  =  328 ;
  int  N_ActionExpr  =  329 ;
  int  N_AssumeDecl  =  330 ;
  int  N_AssumeProve  =  331 ;
  int  N_Assumption  =  332 ;
  int  N_BeginModule  =  333 ;
  int  N_Body  =  334 ;
  int  N_BoundQuant  =  335 ;
  int  N_Case  =  336 ;
  int  N_CaseArm  =  337 ;
//  int  N_CaseStatement  =  338 ;    // Removed by LL 25 Jul 2007
//  int  N_ChooseStatement  =  339 ;  // Removed by LL 25 Jul 2007
  int  N_ConjItem  =  340 ;
  int  N_ConjList  =  341 ;
  int  N_ConsDecl  =  342 ;
  int  N_DisjItem  =  343 ;
  int  N_DisjList  =  344 ;
  int  N_EndModule  =  345 ;
  int  N_Except  =  346 ;
  int  N_ExceptComponent  =  347 ;
  int  N_ExceptSpec  =  348 ;
  int  N_Times =  349 ; // N_Expression
  int  N_Extends  =  350 ;
  int  N_FairnessExpr  =  351 ;
  int  N_FcnAppl  =  352 ;
  int  N_FcnConst  =  353 ;
  int  N_FieldSet  =  354 ;
  int  N_FieldVal  =  355 ;
  int  N_FunctionDefinition  =  356 ;
  int  N_FunctionParam  =  357 ;
  int  N_GeneralId  =  358 ;
  int  N_GenInfixOp  =  359 ;
  int  N_GenNonExpPrefixOp  =  360 ; // I think this is used only for unary -
  int  N_GenPostfixOp  =  361 ;
  int  N_GenPrefixOp  =  362 ;
  int  N_IdentDecl  =  363 ;
  int  N_Real       =  364; //  int  N_Identifier  =  264 ;
  int  N_IdentifierTuple  =  365 ;
  int  N_IdentLHS  =  366 ;
  int  N_IdPrefix  =  367 ;
  int  N_IdPrefixElement  =  368 ;
  int  N_IfThenElse  =  369 ;
  int  N_InfixDecl  =  370 ;
  int  N_InfixExpr  =  371 ;
  int  N_InfixLHS  =  372 ;
  int  N_InfixOp  =  373 ;
  int  N_InnerProof  =  374 ;
  int  N_Instance  =  375 ;
  int  N_NonLocalInstance  =  376 ;
  int  N_Integer  =  377 ;
//  int  N_LeafProof  =  378 ;          // Removed by LL 25 Jul 2007
  int  N_LetDefinitions  =  379 ;
  int  N_LetIn  =  380 ;
  int  N_MaybeBound  =  381 ;
  int  N_Module  =  382 ;
  int  N_ModuleDefinition  =  383 ;
  int  N_NonExpPrefixOp  =  384 ;
  int  N_Number  =  385 ;
  int  N_NumberedAssumeProve  =  386 ;
  int  N_OpApplication  =  387 ;
  int  N_OpArgs  =  388 ;
  int  N_OperatorDefinition  =  389 ;
  int  N_OtherArm  =  390 ;
  int  N_ParamDecl  =  391 ;
  int  N_ParamDeclaration  =  392 ;
  int  N_ParenExpr  =  393 ;
  int  N_PostfixDecl  =  394 ;
  int  N_PostfixExpr  =  395 ;
  int  N_PostfixLHS  =  396 ;
  int  N_PostfixOp  =  397 ;
  int  N_PrefixDecl  =  398 ;
  int  N_PrefixExpr  =  399 ;
  int  N_PrefixLHS  =  400 ;
  int  N_PrefixOp  =  401 ;
  int  N_Proof  =  402 ;
//  int  N_ProofLet  =  403 ;         // Removed by LL 25 Jul 2007
//  int  N_ProofName  =  404 ;        // Removed by LL 25 Jul 2007
//  int  N_ProofStatement  =  405 ;   // Removed by LL 25 Jul 2007
  int  N_ProofStep  =  406 ;        
  int  N_QEDStep  =  407 ;
  int  N_QuantBound  =  408 ;
  int  N_RcdConstructor  =  409 ;
  int  N_RecordComponent  =  410 ;
  int  N_SetEnumerate  =  411 ;
//  int  N_SetExcept  =  412 ;        // Removed by Andrew Helwer 05 Oct 2024
  int  N_SetOfAll  =  413 ;
  int  N_SetOfFcns  =  414 ;
  int  N_SetOfRcds  =  415 ;
//  int  N_SExceptSpec  =  416 ;      // Removed by Andrew Helwer 05 Oct 2024
//  int  N_SFcnDecl  =  417 ;         // Removed by Andrew Helwer 05 Oct 2024
  int  N_String  =  418 ;
  int  N_SubsetOf  =  419 ;
  int  N_Substitution  =  420 ;
  int  N_TempDecl  =  421 ;
  int  N_Theorem  =  422 ;
  int  N_Tuple  =  423 ;
  int  N_UnboundOrBoundChoose  =  424 ;
  int  N_UnboundQuant  =  425 ;
  int  N_VariableDeclaration  =  426 ;
  int  T_IN = 427 ;
  int  T_EQUAL = 428 ;
  int  N_NewSymb = 429 ;     // Added  6 Mar 07 by LL
  int  N_Lambda  = 430 ;     // Added 27 Mar 07 by LL
  int  N_Recursive  = 431 ;  // Added 29 Mar 07 by LL
  int  N_Label  = 432 ;      // Added 21 Apr 07 by LL
  int  N_StructOp = 433 ;
    /***********************************************************************
    * This was added 11 May 07 by LL to represent something like the "<"   *
    * in "!<".  A struct op has a single child that is a SyntaxTreeNode    *
    * either formed from one of the tokens "<<", ">>", "@", or ":" or      *
    * else from a NUMBER_LITERAL token.                                    *
    ***********************************************************************/
  int  N_NumerableStep = 434 ;      // Added 23 Jul 07 by LL
  int  N_TerminalProof = 435 ;      // Added 23 Jul 07 by LL
  int  N_UseOrHide     = 436 ;      // Added 24 Jul 07 by LL
  int  N_NonExprBody   = 437 ;      // Added 24 Jul 07 by LL
  int  N_DefStep       = 438 ;      // Added 25 Jul 07 by LL
  int  N_HaveStep      = 439 ;      // Added 15 Aug 07 by LL
  int  N_TakeStep      = 440 ;      // Added 15 Aug 07 by LL
  int  N_WitnessStep   = 441 ;      // Added 15 Aug 07 by LL
  int  N_PickStep      = 442 ;      // Added 15 Aug 07 by LL
  int  N_CaseStep      = 443 ;      // Added 15 Aug 07 by LL
  int  N_AssertStep    = 444 ;      // Added 15 Aug 07 by LL
    
  UniqueString[] SyntaxNodeImage = { /* name of symbol n should be n+1 lines down*/
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("N_ActDecl"),
    UniqueString.uniqueStringOf("N_ActionExpr"),
    UniqueString.uniqueStringOf("N_AssumeDecl"),
    UniqueString.uniqueStringOf("N_AssumeProve"),
    UniqueString.uniqueStringOf("N_Assumption"),
    UniqueString.uniqueStringOf("N_BeginModule"),
    UniqueString.uniqueStringOf("N_Body"),
    UniqueString.uniqueStringOf("N_BoundedQuant"),
    UniqueString.uniqueStringOf("N_Case"),
    UniqueString.uniqueStringOf("N_CaseArm"),
    UniqueString.uniqueStringOf("N_CaseStatement"),
    UniqueString.uniqueStringOf("N_ChooseStatement"),
    UniqueString.uniqueStringOf("N_ConjItem"),
    UniqueString.uniqueStringOf("N_ConjList"),
    UniqueString.uniqueStringOf("N_ConsDecl"),
    UniqueString.uniqueStringOf("N_DisjItem"),
    UniqueString.uniqueStringOf("N_DisjList"),
    UniqueString.uniqueStringOf("N_EndModule"),
    UniqueString.uniqueStringOf("N_Except"),
    UniqueString.uniqueStringOf("N_ExceptComponent"),
    UniqueString.uniqueStringOf("N_ExceptSpec"),
    UniqueString.uniqueStringOf("N_Times"), // "N_Expression"),
    UniqueString.uniqueStringOf("N_Extends"),
    UniqueString.uniqueStringOf("N_FairnessExpr"),
    UniqueString.uniqueStringOf("N_FcnAppl"),
    UniqueString.uniqueStringOf("N_FcnConst"),
    UniqueString.uniqueStringOf("N_FieldSet"),
    UniqueString.uniqueStringOf("N_FieldVal"),
    UniqueString.uniqueStringOf("N_FunctionDefinition"),
    UniqueString.uniqueStringOf("N_FunctionParam"),
    UniqueString.uniqueStringOf("N_GeneralId"),
    UniqueString.uniqueStringOf("N_GenInfixOp"),
    UniqueString.uniqueStringOf("N_GenNonExpPrefixOp"),
    UniqueString.uniqueStringOf("N_GenPostfixOp"),
    UniqueString.uniqueStringOf("N_GenPrefixOp"),
    UniqueString.uniqueStringOf("N_IdentDecl"),
    UniqueString.uniqueStringOf("N_Real"), // was N_Identifier
    UniqueString.uniqueStringOf("N_IdentifierTuple"),
    UniqueString.uniqueStringOf("N_IdentLHS"),
    UniqueString.uniqueStringOf("N_IdPrefix"),
    UniqueString.uniqueStringOf("N_IdPrefixElement"),
    UniqueString.uniqueStringOf("N_IfThenElse"),
    UniqueString.uniqueStringOf("N_InfixDecl"),
    UniqueString.uniqueStringOf("N_InfixExpr"),
    UniqueString.uniqueStringOf("N_InfixLHS"),
    UniqueString.uniqueStringOf("N_InfixOp"),
    UniqueString.uniqueStringOf("N_InnerProof"),
    UniqueString.uniqueStringOf("N_Instance"),
    UniqueString.uniqueStringOf("N_NonLocalInstance"),
    UniqueString.uniqueStringOf("N_Integer"),
    UniqueString.uniqueStringOf("N_LeafProof"),
    UniqueString.uniqueStringOf("N_LetDefinitions"),
    UniqueString.uniqueStringOf("N_LetIn"),
    UniqueString.uniqueStringOf("N_MaybeBound"),
    UniqueString.uniqueStringOf("N_Module"),
    UniqueString.uniqueStringOf("N_ModuleDefinition"),
    UniqueString.uniqueStringOf("N_NonExpPrefixOp"),
    UniqueString.uniqueStringOf("N_Number"),
    UniqueString.uniqueStringOf("N_NumberedAssumeProve"),
    UniqueString.uniqueStringOf("N_OpApplication"),
    UniqueString.uniqueStringOf("N_OpArgs"),
    UniqueString.uniqueStringOf("N_OperatorDefinition"),
    UniqueString.uniqueStringOf("N_OtherArm"),
    UniqueString.uniqueStringOf("N_ParamDecl"),
    UniqueString.uniqueStringOf("N_ParamDeclaration"),
    UniqueString.uniqueStringOf("N_ParenExpr"),
    UniqueString.uniqueStringOf("N_PostfixDecl"),
    UniqueString.uniqueStringOf("N_PostfixExpr"),
    UniqueString.uniqueStringOf("N_PostfixLHS"),
    UniqueString.uniqueStringOf("N_PostfixOp"),
    UniqueString.uniqueStringOf("N_PrefixDecl"),
    UniqueString.uniqueStringOf("N_PrefixExpr"),
    UniqueString.uniqueStringOf("N_PrefixLHS"),
    UniqueString.uniqueStringOf("N_PrefixOp"),
    UniqueString.uniqueStringOf("N_Proof"),
    UniqueString.uniqueStringOf("N_ProofLet"),
    UniqueString.uniqueStringOf("N_ProofName"),
    UniqueString.uniqueStringOf("N_ProofStatement"),
    UniqueString.uniqueStringOf("N_ProofStep"),
    UniqueString.uniqueStringOf("N_QEDStep"),
    UniqueString.uniqueStringOf("N_QuantBound"),
    UniqueString.uniqueStringOf("N_RcdConstructor"),
    UniqueString.uniqueStringOf("N_RecordComponent"),
    UniqueString.uniqueStringOf("N_SetEnumerate"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("N_SetOfAll"),
    UniqueString.uniqueStringOf("N_SetOfFcns"),
    UniqueString.uniqueStringOf("N_SetOfRcds"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("Not a node"),
    UniqueString.uniqueStringOf("N_String"),
    UniqueString.uniqueStringOf("N_SubsetOf"),
    UniqueString.uniqueStringOf("N_Substitution"),
    UniqueString.uniqueStringOf("N_TempDecl"),
    UniqueString.uniqueStringOf("N_Theorem"),
    UniqueString.uniqueStringOf("N_Tuple"),
    UniqueString.uniqueStringOf("N_UnBoundedOrBoundedChoose"),
    UniqueString.uniqueStringOf("N_UnboundedQuant"),
    UniqueString.uniqueStringOf("N_VariableDeclaration"),
    UniqueString.uniqueStringOf("Token ="),    // I don't understand why this shouldn't be "Token \\in" ??
    UniqueString.uniqueStringOf("Token \\in"), // I don't understand why this shouldn't be "Token =" ??
    UniqueString.uniqueStringOf("N_NewSymb"),  // Added  8 Mar 07 by LL
    UniqueString.uniqueStringOf("N_Lambda"),   // Added 27 Mar 07 by LL
    UniqueString.uniqueStringOf("N_Recursive"),  // Added 29 Mar 07 by LL
    UniqueString.uniqueStringOf("N_Label"),      // Added 21 Apr 07 by LL
    UniqueString.uniqueStringOf("N_StructOp"),
    UniqueString.uniqueStringOf("N_NumerableStep"), // Added 23 Jul 07 by LL
    UniqueString.uniqueStringOf("N_TerminalProof"), // Added 23 Jul 07 by LL
    UniqueString.uniqueStringOf("N_UseOrHide"),     // Added 24 Jul 07 by LL
    UniqueString.uniqueStringOf("N_NonExprBody"),   // Added 24 Jul 07 by LL
    UniqueString.uniqueStringOf("N_DefStep"),       // Added 25 Jul 07 by LL
    UniqueString.uniqueStringOf("N_HaveStep"),      // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("N_TakeStep"),      // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("N_WitnessStep"),   // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("N_PickStep"),      // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("N_CaseStep"),      // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("N_AssertStep"),    // Added 15 Aug 07 by LL
    UniqueString.uniqueStringOf("Not a node")
     };
}

