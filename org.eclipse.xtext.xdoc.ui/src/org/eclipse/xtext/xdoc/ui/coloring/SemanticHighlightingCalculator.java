/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.xdoc.ui.coloring;

import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.ABSTRACT_SECTION__TITLE;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.CHAPTER;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.CODE_BLOCK;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.CODE_BLOCK__CONTENTS;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.EMPHASIZE;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.EMPHASIZE__CONTENTS;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.SECTION;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.SECTION2;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.SECTION3;
import static org.eclipse.xtext.xdoc.xdoc.XdocPackage.Literals.SECTION4;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.NodeUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.xdoc.xdoc.Chapter;
import org.eclipse.xtext.xdoc.xdoc.CodeBlock;
import org.eclipse.xtext.xdoc.xdoc.Emphasize;
import org.eclipse.xtext.xdoc.xdoc.Section;
import org.eclipse.xtext.xdoc.xdoc.Section2;
import org.eclipse.xtext.xdoc.xdoc.Section3;
import org.eclipse.xtext.xdoc.xdoc.Section4;

public class SemanticHighlightingCalculator implements ISemanticHighlightingCalculator {
	
	public void provideHighlightingFor(final XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getContents().isEmpty())
			return;
		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = (EObject) iterator.next();
			highlightingFor.invoke(eObject,acceptor);
		}
	}
	
	private PolymorphicDispatcher<Void> highlightingFor = PolymorphicDispatcher.createForSingleTarget("highlight", 2, 2, this);
	
	protected void highlight(Object obj, IHighlightedPositionAcceptor acceptor) {}
	protected void highlight(Chapter obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, ABSTRACT_SECTION__TITLE, CHAPTER.getName());
	}
	protected void highlight(Section obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, ABSTRACT_SECTION__TITLE, SECTION.getName());
	}
	protected void highlight(Section2 obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, ABSTRACT_SECTION__TITLE, SECTION2.getName());
	}
	protected void highlight(Section3 obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, ABSTRACT_SECTION__TITLE, SECTION3.getName());
	}
	protected void highlight(Section4 obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, ABSTRACT_SECTION__TITLE, SECTION4.getName());
	}
	protected void highlight(Emphasize obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, EMPHASIZE__CONTENTS, EMPHASIZE.getName());
	}
	protected void highlight(CodeBlock obj, IHighlightedPositionAcceptor acceptor) {
		highlightFeature(obj, acceptor, CODE_BLOCK__CONTENTS, CODE_BLOCK.getName());
	}

	
	protected void highlightFeature(EObject obj,
			IHighlightedPositionAcceptor acceptor, EReference feature, String id) {
		List<AbstractNode> list = NodeUtil.findNodesForFeature(obj, feature);
		if (list.isEmpty())
			return;
		AbstractNode node = list.get(0);
		acceptor.addPosition(node.getOffset(), node.getLength(), id);
	}

}