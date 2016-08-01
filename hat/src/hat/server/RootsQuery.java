
/* The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/, and in the file LICENSE.html in the
 * doc directory.
 * 
 * The Original Code is HAT. The Initial Developer of the
 * Original Code is Bill Foote, with contributions from others
 * at JavaSoft/Sun. Portions created by Bill Foote and others
 * at Javasoft/Sun are Copyright (C) 1997-2004. All Rights Reserved.
 * 
 * In addition to the formal license, I ask that you don't
 * change the history or donations files without permission.
 */

package hat.server;

import java.util.Vector;

import hat.model.*;
import hat.util.ArraySorter;
import hat.util.Comparer;

/**
 *
 * @version     1.8, 10/08/98
 * @author      Bill Foote
 */


class RootsQuery extends QueryHandler {

    private boolean includeWeak;

    public RootsQuery(boolean includeWeak) {
	this.includeWeak = includeWeak;
    }

    public void run() {
	int id = parseHex(query);
	JavaHeapObject target = snapshot.findThing(id);
	if (target == null) {
	    startHtml("Object not found for rootset");
	    error("object not found");
	    endHtml();
	    return;
	}
	if (includeWeak) {
	    startHtml("Rootset references to " + target 
			+ " (includes weak refs)");
	} else {
	    startHtml("Rootset references to " + target 
			+ " (excludes weak refs)");
	}
	out.flush();

	ReferenceChain[] refs 
	    = snapshot.rootsetReferencesTo(target, includeWeak);
	ArraySorter.sort(refs, new Comparer() {
	    public int compare(Object lhs, Object rhs) {
		ReferenceChain left = (ReferenceChain) lhs;
		ReferenceChain right = (ReferenceChain) rhs;
		Root leftR = left.getObj().getRoot();
		Root rightR = right.getObj().getRoot();
		int d = leftR.getType() - rightR.getType();
		if (d != 0) {
		    return -d;	// More interesting values are *higher*
		}
		return left.getDepth() - right.getDepth();
	    }
	});

	out.print("<h1>References to ");
	printThing(target);
	out.println("</h1>");
	int lastType = Root.INVALID_TYPE;
	for (int i= 0; i < refs.length; i++) {
	    ReferenceChain ref = refs[i];
	    Root root = ref.getObj().getRoot();
	    if (root.getType() != lastType) {
		lastType = root.getType();
		out.print("<h2>");
		print(root.getTypeName() + " References");
		out.println("</h2>");
	    }
	    out.print("<h3>");
	    printRoot(root);
	    if (root.getReferer() != null) {
		out.print("<small> (from ");
		printThingAnchorTag(root.getReferer().getId());
		print(root.getReferer().toString());
		out.print(")</a></small>");

	    }
	    out.print(" :</h3>");
	    while (ref != null) {
		ReferenceChain next = ref.getNext();
		JavaHeapObject obj = ref.getObj();
		print("--> ");
		printThing(obj);
		if (next != null) {
		    print(" (" + 
			  obj.describeReferenceTo(next.getObj(), snapshot) 
			  + ":)");
		}
		out.println("<br>");
		ref = next;
	    }
	}

	out.println("<h2>Other queries</h2>");

	if (includeWeak) {
	    printAnchorStart();
	    out.print("roots/");
	    printHex(id);
	    out.print("\">");
	    out.println("Exclude weak refs</a><br>");
	    endHtml();
	}

	if (!includeWeak) {
	    printAnchorStart();
	    out.print("allRoots/");
	    printHex(id);
	    out.print("\">");
	    out.println("Include weak refs</a><br>");
	}
    }
    
}
