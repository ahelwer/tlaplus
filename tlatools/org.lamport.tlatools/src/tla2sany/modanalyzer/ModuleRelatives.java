// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
package tla2sany.modanalyzer;

import java.util.ArrayList;
import java.util.List;

import tla2sany.utilities.Vector;

/**
 * This class contains fields representing the names of modules that are all related by EXTENDing or INSTANCEing
 * or inner definition to a ParseUnit (i.e. top level module in its own file)
 */
public class ModuleRelatives {

  ParseUnit     parseUnit;                              // The ParseUnit that THIS ModuleRelatives object is associated with

  ModulePointer currentModule                   = null; // The TreeNode where the name of the current module appears in
                                                        //   in the image field; using the tree node rather than the String
                                                        //   name of the module 

  ModulePointer outerModule                     = null; // TreeNode of the immediate outer (parent) module; 
                                                        //   null currentModule is the outermost in parseUnit

  /**
   * List of ModulePointers for immediate inner modules 
   */
  List<ModulePointer> directInnerModules        = new ArrayList<>(); 

  Vector        directlyExtendedModuleNames     = new Vector(); 
                                                        // Vector of String names for modules mentioned in EXTENDS decls by 
                                                        //   currentModule, whether or not they are resolved within the 
                                                        //   current ParseUnit

  Vector        directlyInstantiatedModuleNames = new Vector(); 
                                                        // Vector of String names for modules directly instantiated 
                                                        //   by currentModule, whether or not they are resolved within the
                                                        //   current ParseUnit

  ModuleContext context = new ModuleContext();          // The context that maps module String names known in this module 
                                                        //   (whether or not they are referenced in it) to ModulePointers


  // constructor
  public ModuleRelatives(ParseUnit pu, ModulePointer node) {
    parseUnit     = pu;
    currentModule = node;
  }

  public String toString() {
    String ret = "currentModuleName: " + currentModule.getName();

    ret += "\nouterModule: " + ( outerModule == null 
				 ? "<null>" 
				 : outerModule.getName() );

    ret += "\ndirectInnerModules: ";
    for (ModulePointer mp : this.directInnerModules) {
      ret += mp.getName() + " ";
    }

    ret += "\ndirectlyExtendedModuleNames: ";
    for (int i = 0; i < directlyExtendedModuleNames.size(); i++) {
      ret += (String)(directlyExtendedModuleNames.elementAt(i)) + " ";
    }

    ret += "\ndirectlyInstantiatedModuleNames: ";
    for (int i = 0; i < directlyInstantiatedModuleNames.size(); i++) {
      ret += (String)(directlyInstantiatedModuleNames.elementAt(i)) + " ";
    }

    ret += "\n" + context.toString();
    return ret;
  }

	public void addExtendee(final String module) {
		directlyExtendedModuleNames.addElement(module);
	}
} // end class
