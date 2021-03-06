/*
* generated by Xtext
*/
package org.eclipse.xtext.xdoc.ui.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import org.eclipse.xtext.xdoc.xdoc.AbstractSection;
import org.eclipse.xtext.xdoc.xdoc.XdocFile;

/**
 * customization of the default outline structure
 * 
 */
public class XdocOutlineTreeProvider extends DefaultOutlineTreeProvider {
	
	protected void _createChildren(DocumentRootNode parentNode, XdocFile document) {
		createChildrenForSections(parentNode, document);
	}
	
	protected void _createChildren(IOutlineNode parentNode, EObject container) {
		createChildrenForSections(parentNode, container);
	}

	private void createChildrenForSections(IOutlineNode parentNode,
			EObject container) {
		for(AbstractSection subSection : EcoreUtil2.typeSelect(container.eContents(), AbstractSection.class)) {
			createNode(parentNode, subSection);
		}
	}
}
