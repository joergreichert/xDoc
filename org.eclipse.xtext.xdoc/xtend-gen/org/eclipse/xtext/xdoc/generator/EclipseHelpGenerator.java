package org.eclipse.xtext.xdoc.generator;

import com.google.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.xbase.lib.BooleanExtensions;
import org.eclipse.xtext.xbase.lib.ComparableExtensions;
import org.eclipse.xtext.xbase.lib.IntegerExtensions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.eclipse.xtext.xdoc.generator.AbstractSectionExtension;
import org.eclipse.xtext.xdoc.generator.PlainText;
import org.eclipse.xtext.xdoc.generator.TocGenerator;
import org.eclipse.xtext.xdoc.generator.util.EclipseNamingExtensions;
import org.eclipse.xtext.xdoc.generator.util.GitExtensions;
import org.eclipse.xtext.xdoc.generator.util.GlossaryExtensions;
import org.eclipse.xtext.xdoc.generator.util.JavaDocExtension;
import org.eclipse.xtext.xdoc.generator.util.Utils;
import org.eclipse.xtext.xdoc.xdoc.AbstractSection;
import org.eclipse.xtext.xdoc.xdoc.Anchor;
import org.eclipse.xtext.xdoc.xdoc.Chapter;
import org.eclipse.xtext.xdoc.xdoc.Code;
import org.eclipse.xtext.xdoc.xdoc.CodeBlock;
import org.eclipse.xtext.xdoc.xdoc.CodeRef;
import org.eclipse.xtext.xdoc.xdoc.Document;
import org.eclipse.xtext.xdoc.xdoc.Emphasize;
import org.eclipse.xtext.xdoc.xdoc.Identifiable;
import org.eclipse.xtext.xdoc.xdoc.ImageRef;
import org.eclipse.xtext.xdoc.xdoc.Item;
import org.eclipse.xtext.xdoc.xdoc.LangDef;
import org.eclipse.xtext.xdoc.xdoc.Link;
import org.eclipse.xtext.xdoc.xdoc.MarkupInCode;
import org.eclipse.xtext.xdoc.xdoc.OrderedList;
import org.eclipse.xtext.xdoc.xdoc.Part;
import org.eclipse.xtext.xdoc.xdoc.Ref;
import org.eclipse.xtext.xdoc.xdoc.Section;
import org.eclipse.xtext.xdoc.xdoc.Section2;
import org.eclipse.xtext.xdoc.xdoc.Section3;
import org.eclipse.xtext.xdoc.xdoc.Section4;
import org.eclipse.xtext.xdoc.xdoc.Table;
import org.eclipse.xtext.xdoc.xdoc.TableData;
import org.eclipse.xtext.xdoc.xdoc.TableRow;
import org.eclipse.xtext.xdoc.xdoc.TextOrMarkup;
import org.eclipse.xtext.xdoc.xdoc.TextPart;
import org.eclipse.xtext.xdoc.xdoc.Todo;
import org.eclipse.xtext.xdoc.xdoc.UnorderedList;
import org.eclipse.xtext.xdoc.xdoc.XdocFile;
import org.eclipse.xtext.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class EclipseHelpGenerator implements IGenerator {
  
  @Inject
  private JavaDocExtension jdoc;
  
  @Inject
  private GitExtensions git;
  
  @Inject
  private Utils utils;
  
  @Inject
  private GlossaryExtensions glossaryExtensions;
  
  @Inject
  private EclipseNamingExtensions eclipseNamingExtensions;
  
  @Inject
  private TocGenerator tocGenerator;
  
  @Inject
  private PlainText plainText;
  
  @Inject
  private AbstractSectionExtension sectionExtension;
  
  public void doGenerate(final Resource res, final IFileSystemAccess access) throws RuntimeException {
    {
      EList<EObject> _contents = res.getContents();
      EObject _head = IterableExtensions.<EObject>head(_contents);
      AbstractSection _mainSection = ((XdocFile) _head)==null?(AbstractSection)null:((XdocFile) _head).getMainSection();
      final AbstractSection doc = _mainSection;
      if ((doc instanceof org.eclipse.xtext.xdoc.xdoc.Document)) {
        this.generate(((Document) doc), access);
      }
    }
  }
  
  public void generate(final Document document, final IFileSystemAccess access) throws RuntimeException {
    {
      StringConcatenation _generateToc = this.tocGenerator.generateToc(document);
      access.generateFile("toc.xml", _generateToc);
      String _fullURL = this.eclipseNamingExtensions.getFullURL(document);
      String _decode = URLDecoder.decode(_fullURL);
      StringConcatenation _generateRootDocument = this.generateRootDocument(document);
      access.generateFile(_decode, _generateRootDocument);
      EList<Chapter> _chapters = document.getChapters();
      for (final Chapter c : _chapters) {
        this.generate(c, access);
      }
      EList<Part> _parts = document.getParts();
      for (final Part p : _parts) {
        this.generate(p, access);
      }
    }
  }
  
  public Object copy(final String fromRelativeFileName, final Resource res) throws RuntimeException {
    Object _xtrycatchfinallyexpression = null;
    try {
      Object _xblockexpression = null;
      {
        int _operator_multiply = IntegerExtensions.operator_multiply(((Integer)16), ((Integer)1024));
        ByteBuffer _allocateDirect = ByteBuffer.allocateDirect(_operator_multiply);
        final ByteBuffer buffer = _allocateDirect;
        URI _uRI = res.getURI();
        final URI uri = _uRI;
        final String sepChar = File.separator;
        String relOutDirRoot = "";
        String inDir = "";
        Object _xifexpression = null;
        boolean _isPlatformResource = uri.isPlatformResource();
        if (_isPlatformResource) {
          {
            URI _trimSegments = uri.trimSegments(1);
            String _string = _trimSegments.toString();
            String _operator_plus = StringExtensions.operator_plus(_string, "/");
            String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, fromRelativeFileName);
            URI _createURI = URI.createURI(_operator_plus_1);
            final URI inPath = _createURI;
            URI _trimSegments_1 = uri.trimSegments(2);
            URI _appendSegment = _trimSegments_1.appendSegment("contents");
            String _string_1 = _appendSegment.toString();
            String _operator_plus_2 = StringExtensions.operator_plus(_string_1, "/");
            String _operator_plus_3 = StringExtensions.operator_plus(_operator_plus_2, fromRelativeFileName);
            URI _createURI_1 = URI.createURI(_operator_plus_3);
            final URI outPath = _createURI_1;
            ResourceSet _resourceSet = res.getResourceSet();
            URIConverter _uRIConverter = _resourceSet.getURIConverter();
            InputStream _createInputStream = _uRIConverter.createInputStream(inPath);
            ReadableByteChannel _newChannel = Channels.newChannel(_createInputStream);
            final ReadableByteChannel inChannel = _newChannel;
            ResourceSet _resourceSet_1 = res.getResourceSet();
            URIConverter _uRIConverter_1 = _resourceSet_1.getURIConverter();
            OutputStream _createOutputStream = _uRIConverter_1.createOutputStream(outPath);
            WritableByteChannel _newChannel_1 = Channels.newChannel(_createOutputStream);
            final WritableByteChannel outChannel = _newChannel_1;
            int _read = inChannel.read(buffer);
            int _operator_minus = IntegerExtensions.operator_minus(1);
            boolean _operator_notEquals = ObjectExtensions.operator_notEquals(((Integer)_read), ((Integer)_operator_minus));
            Boolean _xwhileexpression = _operator_notEquals;
            while (_xwhileexpression) {
              {
                buffer.flip();
                outChannel.write(buffer);
                buffer.compact();
              }
              int _read_1 = inChannel.read(buffer);
              int _operator_minus_1 = IntegerExtensions.operator_minus(1);
              boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(((Integer)_read_1), ((Integer)_operator_minus_1));
              _xwhileexpression = _operator_notEquals_1;
            }
            buffer.flip();
            boolean _hasRemaining = buffer.hasRemaining();
            Boolean _xwhileexpression_1 = _hasRemaining;
            while (_xwhileexpression_1) {
              outChannel.write(buffer);
              boolean _hasRemaining_1 = buffer.hasRemaining();
              _xwhileexpression_1 = _hasRemaining_1;
            }
            outChannel.close();
          }
        }
        _xblockexpression = (_xifexpression);
      }
      _xtrycatchfinallyexpression = _xblockexpression;
    } catch (final Exception e) { 
      RuntimeException _runtimeException = new RuntimeException(e);
      throw _runtimeException;
    }
    return _xtrycatchfinallyexpression;
  }
  
  public StringConcatenation generateRootDocument(final Document document) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<html>");
    _builder.newLine();
    _builder.append("<head>");
    _builder.newLine();
    _builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" >");
    _builder.newLine();
    _builder.append("<title>");
    TextOrMarkup _title = document.getTitle();
    CharSequence _genPlainText = this.plainText.genPlainText(_title);
    _builder.append(_genPlainText, "");
    _builder.append("</title>");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<link href=\"book.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link href=\"code.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link rel=\"home\" href=\"");
    String _fullURL = this.eclipseNamingExtensions.getFullURL(document);
    _builder.append(_fullURL, "");
    _builder.append("\" title=\"\">");
    _builder.newLineIfNotEmpty();
    _builder.append("</head>");
    _builder.newLine();
    _builder.append("<body>");
    _builder.newLine();
    _builder.append("<h1>");
    TextOrMarkup _title_1 = document.getTitle();
    CharSequence _genPlainText_1 = this.plainText.genPlainText(_title_1);
    _builder.append(_genPlainText_1, "");
    _builder.append("</h1>");
    _builder.newLineIfNotEmpty();
    {
      EList<TextOrMarkup> _contents = document.getContents();
      for(final TextOrMarkup content : _contents) {
        StringConcatenation _generatePar = this.generatePar(content);
        _builder.append(_generatePar, "");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      List<? extends AbstractSection> _sections = this.sectionExtension.sections(document);
      boolean hasAnyElements = false;
      for(final AbstractSection ss : _sections) {
        if (!hasAnyElements) {
          hasAnyElements = true;
          _builder.append("<ol>", "");
        }
        StringConcatenation _generateEntryInRoot = this.generateEntryInRoot(ss);
        _builder.append(_generateEntryInRoot, "");
        _builder.newLineIfNotEmpty();
      }
      if (hasAnyElements) {
        _builder.append("</ol>", "");
      }
    }
    _builder.append("</body>");
    _builder.newLine();
    _builder.append("</html>");
    _builder.newLine();
    return _builder;
  }
  
  public StringConcatenation generateEntryInRoot(final AbstractSection section) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<li><a href=\"");
    String _fullURL = this.eclipseNamingExtensions.getFullURL(section);
    _builder.append(_fullURL, "");
    _builder.append("\">");
    TextOrMarkup _title = section.getTitle();
    CharSequence _genPlainText = this.plainText.genPlainText(_title);
    _builder.append(_genPlainText, "");
    _builder.append("</a>");
    _builder.newLineIfNotEmpty();
    {
      List<? extends AbstractSection> _sections = this.sectionExtension.sections(section);
      boolean hasAnyElements = false;
      for(final AbstractSection ss : _sections) {
        if (!hasAnyElements) {
          hasAnyElements = true;
          _builder.append("<ol>", "	");
        }
        _builder.append("\t");
        StringConcatenation _generateEntryInRoot = this.generateEntryInRoot(ss);
        _builder.append(_generateEntryInRoot, "	");
        _builder.newLineIfNotEmpty();
      }
      if (hasAnyElements) {
        _builder.append("</ol>", "	");
      }
    }
    _builder.append("</li>");
    _builder.newLine();
    return _builder;
  }
  
  protected void _generate(final Chapter chapter, final IFileSystemAccess access) throws RuntimeException {
    String _fullURL = this.eclipseNamingExtensions.getFullURL(chapter);
    String _decode = URLDecoder.decode(_fullURL);
    CharSequence _generate = this.generate(chapter);
    access.generateFile(_decode, _generate);
  }
  
  protected void _generate(final Part part, final IFileSystemAccess fsa) throws RuntimeException {
    {
      String _fullURL = this.eclipseNamingExtensions.getFullURL(part);
      String _decode = URLDecoder.decode(_fullURL);
      CharSequence _generate = this.generate(part);
      fsa.generateFile(_decode, _generate);
      EList<Chapter> _chapters = part.getChapters();
      for (final Chapter c : _chapters) {
        this.generate(c, fsa);
      }
    }
  }
  
  protected CharSequence _generate(final Part part) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<html>");
    _builder.newLine();
    _builder.append("<head>");
    _builder.newLine();
    _builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" >");
    _builder.newLine();
    _builder.append("<title>");
    TextOrMarkup _title = part.getTitle();
    CharSequence _genPlainText = this.plainText.genPlainText(_title);
    _builder.append(_genPlainText, "");
    _builder.append("</title>");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<link href=\"book.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link href=\"code.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link rel=\"home\" href=\"index.html\" title=\"\">");
    _builder.newLine();
    _builder.append("</head>");
    _builder.newLine();
    _builder.append("<body>");
    _builder.newLine();
    _builder.append("<a name=\"");
    String _localId = this.eclipseNamingExtensions.getLocalId(part);
    _builder.append(_localId, "");
    _builder.append("\"></a>");
    _builder.newLineIfNotEmpty();
    _builder.append("<");
    String _headtag = this.headtag(part);
    _builder.append(_headtag, "");
    _builder.append(">");
    TextOrMarkup _title_1 = part.getTitle();
    CharSequence _genPlainText_1 = this.plainText.genPlainText(_title_1);
    _builder.append(_genPlainText_1, "");
    _builder.append("</");
    String _headtag_1 = this.headtag(part);
    _builder.append(_headtag_1, "");
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      List<? extends AbstractSection> _sections = this.sectionExtension.sections(part);
      boolean hasAnyElements = false;
      for(final AbstractSection ss : _sections) {
        if (!hasAnyElements) {
          hasAnyElements = true;
          _builder.append("<ol>", "");
        }
        StringConcatenation _generateEntryInRoot = this.generateEntryInRoot(ss);
        _builder.append(_generateEntryInRoot, "");
        _builder.newLineIfNotEmpty();
      }
      if (hasAnyElements) {
        _builder.append("</ol>", "");
      }
    }
    _builder.append("</body>");
    _builder.newLine();
    _builder.append("</html>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Chapter chapter) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<html>");
    _builder.newLine();
    _builder.append("<head>");
    _builder.newLine();
    _builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" >");
    _builder.newLine();
    _builder.append("<title>");
    TextOrMarkup _title = chapter.getTitle();
    CharSequence _genPlainText = this.plainText.genPlainText(_title);
    _builder.append(_genPlainText, "");
    _builder.append("</title>");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<link href=\"book.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link href=\"code.css\" rel=\"stylesheet\" type=\"text/css\">");
    _builder.newLine();
    _builder.append("<link rel=\"home\" href=\"index.html\" title=\"\">");
    _builder.newLine();
    _builder.append("</head>");
    _builder.newLine();
    _builder.append("<body>");
    _builder.newLine();
    _builder.append("<a name=\"");
    String _localId = this.eclipseNamingExtensions.getLocalId(chapter);
    _builder.append(_localId, "");
    _builder.append("\"></a>");
    _builder.newLineIfNotEmpty();
    _builder.append("<");
    String _headtag = this.headtag(chapter);
    _builder.append(_headtag, "");
    _builder.append(">");
    TextOrMarkup _title_1 = chapter.getTitle();
    CharSequence _genPlainText_1 = this.plainText.genPlainText(_title_1);
    _builder.append(_genPlainText_1, "");
    _builder.append("</");
    String _headtag_1 = this.headtag(chapter);
    _builder.append(_headtag_1, "");
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      EList<TextOrMarkup> _contents = chapter.getContents();
      for(final TextOrMarkup content : _contents) {
        StringConcatenation _generatePar = this.generatePar(content);
        _builder.append(_generatePar, "");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      List<? extends AbstractSection> _sections = this.sectionExtension.sections(chapter);
      for(final AbstractSection ss : _sections) {
        CharSequence _generate = this.generate(ss);
        _builder.append(_generate, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</body>");
    _builder.newLine();
    _builder.append("</html>");
    _builder.newLine();
    return _builder;
  }
  
  public String headtag(final AbstractSection section) {
    String _switchResult = null;
    final AbstractSection section_1 = section;
    boolean matched = false;
    if (!matched) {
      if (section_1 instanceof Part) {
        final Part section_2 = (Part) section_1;
        matched=true;
        _switchResult = "h1";
      }
    }
    if (!matched) {
      if (section_1 instanceof Chapter) {
        final Chapter section_3 = (Chapter) section_1;
        matched=true;
        _switchResult = "h1";
      }
    }
    if (!matched) {
      if (section_1 instanceof Section) {
        final Section section_4 = (Section) section_1;
        matched=true;
        _switchResult = "h2";
      }
    }
    if (!matched) {
      if (section_1 instanceof Section2) {
        final Section2 section_5 = (Section2) section_1;
        matched=true;
        _switchResult = "h3";
      }
    }
    if (!matched) {
      if (section_1 instanceof Section3) {
        final Section3 section_6 = (Section3) section_1;
        matched=true;
        _switchResult = "h4";
      }
    }
    if (!matched) {
      if (section_1 instanceof Section4) {
        final Section4 section_7 = (Section4) section_1;
        matched=true;
        _switchResult = "h5";
      }
    }
    if (!matched) {
      _switchResult = "h1";
    }
    return _switchResult;
  }
  
  protected CharSequence _generate(final AbstractSection section) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<a name=\"");
    String _localId = this.eclipseNamingExtensions.getLocalId(section);
    _builder.append(_localId, "");
    _builder.append("\"></a>");
    _builder.newLineIfNotEmpty();
    _builder.append("<");
    String _headtag = this.headtag(section);
    _builder.append(_headtag, "");
    _builder.append(">");
    TextOrMarkup _title = section.getTitle();
    CharSequence _genPlainText = this.plainText.genPlainText(_title);
    _builder.append(_genPlainText, "");
    _builder.append("</");
    String _headtag_1 = this.headtag(section);
    _builder.append(_headtag_1, "");
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      EList<TextOrMarkup> _contents = section.getContents();
      for(final TextOrMarkup c : _contents) {
        StringConcatenation _generatePar = this.generatePar(c);
        _builder.append(_generatePar, "");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      List<? extends AbstractSection> _sections = this.sectionExtension.sections(section);
      for(final AbstractSection ss : _sections) {
        CharSequence _generate = this.generate(ss);
        _builder.append(_generate, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  protected CharSequence _generate(final Section4 aS) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<a name=\"");
    String _localId = this.eclipseNamingExtensions.getLocalId(aS);
    _builder.append(_localId, "");
    _builder.append("\"></a>");
    _builder.newLineIfNotEmpty();
    _builder.append("<h5>");
    TextOrMarkup _title = aS.getTitle();
    StringConcatenation _genNonParContent = this.genNonParContent(_title);
    _builder.append(_genNonParContent, "");
    _builder.append("</h5>");
    _builder.newLineIfNotEmpty();
    {
      EList<TextOrMarkup> _contents = aS.getContents();
      for(final TextOrMarkup tom : _contents) {
        StringConcatenation _generatePar = this.generatePar(tom);
        _builder.append(_generatePar, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public StringConcatenation generatePar(final TextOrMarkup tom) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<p>");
    _builder.newLine();
    {
      EList<EObject> _contents = tom.getContents();
      for(final EObject c : _contents) {
        CharSequence _generate = this.generate(c);
        _builder.append(_generate, "");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("</p>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Todo todo) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<div class=\"todo\" >");
    _builder.newLine();
    String _text = todo.getText();
    _builder.append(_text, "");
    _builder.newLineIfNotEmpty();
    _builder.append("</div>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Ref ref) {
    StringConcatenation _xblockexpression = null;
    {
      StringConcatenation _xifexpression = null;
      Identifiable _ref = ref.getRef();
      if ((_ref instanceof org.eclipse.xtext.xdoc.xdoc.AbstractSection)) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("title=\"Go to &quot;");
        Identifiable _ref_1 = ref.getRef();
        TextOrMarkup _title = ((AbstractSection) _ref_1).getTitle();
        CharSequence _genPlainText = this.plainText.genPlainText(_title);
        _builder.append(_genPlainText, "");
        _builder.append("&quot;\"");
        _xifexpression = _builder;
      }
      final StringConcatenation title = _xifexpression;
      StringConcatenation _builder_1 = new StringConcatenation();
      {
        EList<TextOrMarkup> _contents = ref.getContents();
        boolean _isEmpty = _contents.isEmpty();
        if (_isEmpty) {
          _builder_1.append("<a href=\"");
          Identifiable _ref_2 = ref.getRef();
          String _fullURL = this.eclipseNamingExtensions.getFullURL(_ref_2);
          _builder_1.append(_fullURL, "");
          _builder_1.append("\" ");
          _builder_1.append(title, "");
          _builder_1.append(" >section ");
          Identifiable _ref_3 = ref.getRef();
          String _name = _ref_3.getName();
          _builder_1.append(_name, "");
          _builder_1.append("</a>");} else {
          _builder_1.append("<a href=\"");
          Identifiable _ref_4 = ref.getRef();
          String _fullURL_1 = this.eclipseNamingExtensions.getFullURL(_ref_4);
          _builder_1.append(_fullURL_1, "");
          _builder_1.append("\" ");
          _builder_1.append(title, "");
          _builder_1.append(">");
          {
            EList<TextOrMarkup> _contents_1 = ref.getContents();
            for(final TextOrMarkup tom : _contents_1) {
              StringConcatenation _genNonParContent = this.genNonParContent(tom);
              _builder_1.append(_genNonParContent, "");
            }
          }
          _builder_1.append("</a>");
        }
      }
      _xblockexpression = (_builder_1);
    }
    return _xblockexpression;
  }
  
  protected CharSequence _generate(final TextOrMarkup tom) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<EObject> _contents = tom.getContents();
      for(final EObject obj : _contents) {
        CharSequence _generate = this.generate(obj);
        _builder.append(_generate, "");
      }
    }
    return _builder;
  }
  
  protected CharSequence _generate(final UnorderedList ul) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<ul>");
    _builder.newLine();
    {
      EList<Item> _items = ul.getItems();
      for(final Item i : _items) {
        _builder.append("\t");
        CharSequence _generate = this.generate(i);
        _builder.append(_generate, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</ul>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final OrderedList ul) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<ol>");
    _builder.newLine();
    {
      EList<Item> _items = ul.getItems();
      for(final Item i : _items) {
        _builder.append("\t");
        CharSequence _generate = this.generate(i);
        _builder.append(_generate, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</ol>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Item i) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<li>");
    _builder.newLine();
    {
      EList<TextOrMarkup> _contents = i.getContents();
      for(final TextOrMarkup tom : _contents) {
        _builder.append("\t");
        CharSequence _generate = this.generate(tom);
        _builder.append(_generate, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</li>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Anchor a) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<a name=\"anchor-");
    String _name = a.getName();
    _builder.append(_name, "");
    _builder.append("\"></a>");
    return _builder;
  }
  
  protected CharSequence _generate(final ImageRef img) throws RuntimeException {
    StringConcatenation _xblockexpression = null;
    {
      String _path = img.getPath();
      Resource _eResource = img.eResource();
      this.copy(_path, _eResource);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<div class=\"image\" >");
      _builder.newLine();
      {
        String _name = img.getName();
        boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_name, null);
        if (_operator_notEquals) {
          String _name_1 = img.getName();
          StringConcatenation _genLabel = this.genLabel(_name_1);
          _builder.append(_genLabel, "");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append("", "");
      _builder.newLineIfNotEmpty();
      _builder.append("<img src=\"");
      String _path_1 = img.getPath();
      String _unescapeXdocChars = this.utils.unescapeXdocChars(_path_1);
      _builder.append(_unescapeXdocChars, "");
      _builder.append("\" ");
      {
        String _clazz = img.getClazz();
        boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(_clazz, null);
        if (_operator_notEquals_1) {
          _builder.append("class=\"");
          String _clazz_1 = img.getClazz();
          String _unescapeXdocChars_1 = this.utils.unescapeXdocChars(_clazz_1);
          _builder.append(_unescapeXdocChars_1, "");
          _builder.append("\" ");
        }
      }
      _builder.newLineIfNotEmpty();
      {
        boolean _operator_and = false;
        String _style = img.getStyle();
        boolean _operator_notEquals_2 = ObjectExtensions.operator_notEquals(_style, null);
        if (!_operator_notEquals_2) {
          _operator_and = false;
        } else {
          String _style_1 = img.getStyle();
          int _length = _style_1.length();
          boolean _operator_equals = ObjectExtensions.operator_equals(((Integer)_length), ((Integer)0));
          boolean _operator_not = BooleanExtensions.operator_not(_operator_equals);
          _operator_and = BooleanExtensions.operator_and(_operator_notEquals_2, _operator_not);
        }
        if (_operator_and) {
          _builder.append(" style=\"");
          String _style_2 = img.getStyle();
          String _unescapeXdocChars_2 = this.utils.unescapeXdocChars(_style_2);
          _builder.append(_unescapeXdocChars_2, "");
          _builder.append("\" ");
        }
      }
      _builder.append("/>");
      _builder.newLineIfNotEmpty();
      _builder.append("<div class=\"caption\">");
      _builder.newLine();
      String _caption = img.getCaption();
      String _unescapeXdocChars_3 = this.utils.unescapeXdocChars(_caption);
      String _escapeHTMLChars = this.utils.escapeHTMLChars(_unescapeXdocChars_3);
      _builder.append(_escapeHTMLChars, "");
      _builder.newLineIfNotEmpty();
      _builder.append("</div>");
      _builder.newLine();
      _builder.append("</div>");
      _builder.newLine();
      _xblockexpression = (_builder);
    }
    return _xblockexpression;
  }
  
  public StringConcatenation genLabel(final String name) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(this, null);
      if (_operator_notEquals) {
        _builder.append("<a name=\"");
        _builder.append(name, "");
        _builder.append("\"></a>");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _generate(final TextPart tp) {
    String _text = tp.getText();
    String _unescapeXdocChars = this.utils.unescapeXdocChars(_text);
    String _escapeHTMLChars = this.utils.escapeHTMLChars(_unescapeXdocChars);
    return _escapeHTMLChars;
  }
  
  protected CharSequence _generate(final Table table) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<table>");
    _builder.newLine();
    {
      EList<TableRow> _rows = table.getRows();
      for(final TableRow tr : _rows) {
        CharSequence _generate = this.generate(tr);
        _builder.append(_generate, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</table>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final TableRow tr) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<tr>");
    _builder.newLine();
    {
      EList<TableData> _data = tr.getData();
      for(final TableData td : _data) {
        CharSequence _generate = this.generate(td);
        _builder.append(_generate, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</tr>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final TableData td) throws RuntimeException {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<td>");
    _builder.newLine();
    {
      EList<TextOrMarkup> _contents = td.getContents();
      for(final TextOrMarkup c : _contents) {
        CharSequence _generate = this.generate(c);
        _builder.append(_generate, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("</td>");
    _builder.newLine();
    return _builder;
  }
  
  protected CharSequence _generate(final Emphasize em) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<em>");
    {
      EList<TextOrMarkup> _contents = em.getContents();
      for(final TextOrMarkup c : _contents) {
        CharSequence _generate = this.generate(c);
        _builder.append(_generate, "");
      }
    }
    _builder.append("</em>");
    return _builder;
  }
  
  protected CharSequence _generate(final Link link) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<a href=\"");
    String _url = link.getUrl();
    _builder.append(_url, "");
    _builder.append("\">");
    String _text = link.getText();
    String _unescapeXdocChars = this.utils.unescapeXdocChars(_text);
    String _escapeHTMLChars = this.utils.escapeHTMLChars(_unescapeXdocChars);
    _builder.append(_escapeHTMLChars, "");
    _builder.append("</a>");
    return _builder;
  }
  
  protected CharSequence _generate(final CodeRef cRef) {
    StringConcatenation _xblockexpression = null;
    {
      String _xifexpression = null;
      boolean _operator_and = false;
      JvmDeclaredType _element = cRef.getElement();
      if (!(_element instanceof org.eclipse.xtext.common.types.JvmAnnotationType)) {
        _operator_and = false;
      } else {
        TextOrMarkup _altText = cRef.getAltText();
        boolean _operator_equals = ObjectExtensions.operator_equals(_altText, null);
        _operator_and = BooleanExtensions.operator_and((_element instanceof org.eclipse.xtext.common.types.JvmAnnotationType), _operator_equals);
      }
      if (_operator_and) {
        _xifexpression = "@";
      }
      final String prefix = _xifexpression;
      JvmDeclaredType _element_1 = cRef.getElement();
      String _genJavaDocLink = this.jdoc.genJavaDocLink(_element_1);
      final String jDocLink = _genJavaDocLink;
      JvmDeclaredType _element_2 = cRef.getElement();
      String _gitLink = this.git.gitLink(_element_2);
      final String gitLink = _gitLink;
      JvmDeclaredType _element_3 = cRef.getElement();
      char _charAt = ".".charAt(0);
      String _qualifiedName = _element_3.getQualifiedName(_charAt);
      String _unescapeXdocChars = this.utils.unescapeXdocChars(_qualifiedName);
      String _escapeHTMLChars = this.utils.escapeHTMLChars(_unescapeXdocChars);
      final String fqn = _escapeHTMLChars;
      CharSequence _xifexpression_1 = null;
      TextOrMarkup _altText_1 = cRef.getAltText();
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_altText_1, null);
      if (_operator_notEquals) {
        TextOrMarkup _altText_2 = cRef.getAltText();
        CharSequence _generate = this.generate(_altText_2);
        _xifexpression_1 = _generate;
      } else {
        JvmDeclaredType _element_4 = cRef.getElement();
        String _dottedSimpleName = this.dottedSimpleName(_element_4);
        _xifexpression_1 = _dottedSimpleName;
      }
      final CharSequence text = _xifexpression_1;
      StringConcatenation _xifexpression_2 = null;
      boolean _operator_notEquals_1 = ObjectExtensions.operator_notEquals(jDocLink, null);
      if (_operator_notEquals_1) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("<a class=\"jdoc\" href=\"");
        JvmDeclaredType _element_5 = cRef.getElement();
        String _genJavaDocLink_1 = this.jdoc.genJavaDocLink(_element_5);
        _builder.append(_genJavaDocLink_1, "");
        _builder.append("\" title=\"View JavaDoc\"><abbr title=\"");
        _builder.append(fqn, "");
        _builder.append("\" >");
        _builder.append(prefix, "");
        _builder.append(text, "");
        _builder.append("</abbr></a>");
        _xifexpression_2 = _builder;
      } else {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("<abbr title=\"");
        _builder_1.append(fqn, "");
        _builder_1.append("\" >");
        _builder_1.append(prefix, "");
        _builder_1.append(text, "");
        _builder_1.append("</abbr>");
        _xifexpression_2 = _builder_1;
      }
      StringConcatenation ret = _xifexpression_2;
      StringConcatenation _xifexpression_3 = null;
      boolean _operator_notEquals_2 = ObjectExtensions.operator_notEquals(gitLink, null);
      if (_operator_notEquals_2) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append(ret, "");
        _builder_2.append(" <a class=\"srcLink\" href=\"");
        _builder_2.append(gitLink, "");
        _builder_2.append("\" title=\"View Source Code\" >(src)</a>");
        _xifexpression_3 = _builder_2;
      } else {
        _xifexpression_3 = ret;
      }
      _xblockexpression = (_xifexpression_3);
    }
    return _xblockexpression;
  }
  
  public String dottedSimpleName(final JvmDeclaredType type) {
    String _xifexpression = null;
    JvmDeclaredType _declaringType = type.getDeclaringType();
    boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_declaringType, null);
    if (_operator_notEquals) {
      JvmDeclaredType _declaringType_1 = type.getDeclaringType();
      String _dottedSimpleName = this.dottedSimpleName(_declaringType_1);
      String _operator_plus = StringExtensions.operator_plus(_dottedSimpleName, ".");
      String _simpleName = type.getSimpleName();
      String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _simpleName);
      _xifexpression = _operator_plus_1;
    } else {
      String _simpleName_1 = type.getSimpleName();
      _xifexpression = _simpleName_1;
    }
    return _xifexpression;
  }
  
  protected CharSequence _generate(final CodeBlock cb) {
    StringConcatenation _xifexpression = null;
    boolean _isInlineCode = this.utils.isInlineCode(cb);
    if (_isInlineCode) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<span class=\"inlinecode\">");
      EList<EObject> _contents = cb.getContents();
      EObject _head = IterableExtensions.<EObject>head(_contents);
      StringConcatenation _generateCode = this.generateCode(((Code) _head));
      LangDef _language = cb.getLanguage();
      String _formatCode = this.utils.formatCode(_generateCode, _language);
      _builder.append(_formatCode, "");
      _builder.append("</span>");
      _xifexpression = _builder;
    } else {
      StringConcatenation _xblockexpression = null;
      {
        Integer _calcIndent = this.utils.calcIndent(cb);
        final Integer indentToRemove = _calcIndent;
        Iterable<EObject> _xifexpression_1 = null;
        EList<EObject> _contents_1 = cb.getContents();
        int _size = _contents_1.size();
        boolean _operator_greaterThan = ComparableExtensions.<Integer>operator_greaterThan(((Integer)_size), ((Integer)2));
        if (_operator_greaterThan) {
          EList<EObject> _contents_2 = cb.getContents();
          Iterable<EObject> _tail = IterableExtensions.<EObject>tail(_contents_2);
          EList<EObject> _contents_3 = cb.getContents();
          int _size_1 = _contents_3.size();
          int _operator_minus = IntegerExtensions.operator_minus(((Integer)_size_1), ((Integer)2));
          Iterable<EObject> _take = IterableExtensions.<EObject>take(_tail, _operator_minus);
          _xifexpression_1 = _take;
        } else {
          _xifexpression_1 = Collections.EMPTY_LIST;
        }
        final Iterable<EObject> list = _xifexpression_1;
        EList<EObject> _contents_4 = cb.getContents();
        EObject _head_1 = IterableExtensions.<EObject>head(_contents_4);
        StringConcatenation _generateCode_1 = this.generateCode(_head_1);
        String _trimLines = this.trimLines(_generateCode_1, indentToRemove);
        String _replaceAll = _trimLines.replaceAll("\\A(\\s*\n)*", "");
        final String first = _replaceAll;
        String _xifexpression_2 = null;
        EList<EObject> _contents_5 = cb.getContents();
        EObject _last = IterableExtensions.<EObject>last(_contents_5);
        EList<EObject> _contents_6 = cb.getContents();
        EObject _head_2 = IterableExtensions.<EObject>head(_contents_6);
        boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_last, _head_2);
        if (_operator_notEquals) {
          EList<EObject> _contents_7 = cb.getContents();
          EObject _last_1 = IterableExtensions.<EObject>last(_contents_7);
          StringConcatenation _generateCode_2 = this.generateCode(_last_1);
          String _trimLines_1 = this.trimLines(_generateCode_2, indentToRemove);
          String _replaceAll_1 = _trimLines_1.replaceAll("(\\s*\n)*\\Z", "");
          _xifexpression_2 = _replaceAll_1;
        } else {
          String _xblockexpression_1 = null;
          {
            first.replaceAll("(\\s*\n)*\\Z", "");
            _xblockexpression_1 = ("");
          }
          _xifexpression_2 = _xblockexpression_1;
        }
        final String last = _xifexpression_2;
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("<div class=\"literallayout\">");
        _builder_1.newLine();
        _builder_1.append("<div class=\"incode\">");
        _builder_1.newLine();
        _builder_1.append("<p class=\"code\">");
        _builder_1.newLine();
        LangDef _language_1 = cb.getLanguage();
        String _formatCode_1 = this.utils.formatCode(first, _language_1);
        _builder_1.append(_formatCode_1, "");
        _builder_1.newLineIfNotEmpty();
        {
          for(final EObject code : list) {
            StringConcatenation _generateCode_3 = this.generateCode(code);
            String _trimLines_2 = this.trimLines(_generateCode_3, indentToRemove);
            LangDef _language_2 = cb.getLanguage();
            String _formatCode_2 = this.utils.formatCode(_trimLines_2, _language_2);
            _builder_1.append(_formatCode_2, "");
            _builder_1.newLineIfNotEmpty();
          }
        }
        LangDef _language_3 = cb.getLanguage();
        String _formatCode_3 = this.utils.formatCode(last, _language_3);
        _builder_1.append(_formatCode_3, "");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("</p>");
        _builder_1.newLine();
        _builder_1.append("</div>");
        _builder_1.newLine();
        _builder_1.append("</div>");
        _builder_1.newLine();
        _xblockexpression = (_builder_1);
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  public String trimLines(final CharSequence cs, final int amount) {
    String _string = cs.toString();
    String _operator_plus = StringExtensions.operator_plus("\n\\s{", ((Integer)amount));
    String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, "}");
    String _replaceAll = _string.replaceAll(_operator_plus_1, "\n");
    return _replaceAll;
  }
  
  protected StringConcatenation _generateCode(final Code code) {
    StringConcatenation _builder = new StringConcatenation();
    String _contents = code.getContents();
    String _unescapeXdocChars = this.utils.unescapeXdocChars(_contents);
    _builder.append(_unescapeXdocChars, "");
    return _builder;
  }
  
  protected StringConcatenation _generateCode(final MarkupInCode code) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _generate = this.generate(code);
    _builder.append(_generate, "");
    return _builder;
  }
  
  public StringConcatenation genNonParContent(final TextOrMarkup tom) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<EObject> _contents = tom.getContents();
      for(final EObject obj : _contents) {
        CharSequence _generate = this.generate(obj);
        _builder.append(_generate, "");
      }
    }
    return _builder;
  }
  
  public void generate(final AbstractSection chapter, final IFileSystemAccess access) throws RuntimeException {
    if ((chapter instanceof Chapter)
         && (access instanceof IFileSystemAccess)) {
      _generate((Chapter)chapter, (IFileSystemAccess)access);
    } else if ((chapter instanceof Part)
         && (access instanceof IFileSystemAccess)) {
      _generate((Part)chapter, (IFileSystemAccess)access);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        java.util.Arrays.<Object>asList(chapter, access).toString());
    }
  }
  
  public CharSequence generate(final EObject chapter) throws RuntimeException {
    if ((chapter instanceof Chapter)) {
      return _generate((Chapter)chapter);
    } else if ((chapter instanceof Part)) {
      return _generate((Part)chapter);
    } else if ((chapter instanceof Section4)) {
      return _generate((Section4)chapter);
    } else if ((chapter instanceof AbstractSection)) {
      return _generate((AbstractSection)chapter);
    } else if ((chapter instanceof Anchor)) {
      return _generate((Anchor)chapter);
    } else if ((chapter instanceof CodeBlock)) {
      return _generate((CodeBlock)chapter);
    } else if ((chapter instanceof CodeRef)) {
      return _generate((CodeRef)chapter);
    } else if ((chapter instanceof Emphasize)) {
      return _generate((Emphasize)chapter);
    } else if ((chapter instanceof ImageRef)) {
      return _generate((ImageRef)chapter);
    } else if ((chapter instanceof Link)) {
      return _generate((Link)chapter);
    } else if ((chapter instanceof OrderedList)) {
      return _generate((OrderedList)chapter);
    } else if ((chapter instanceof Ref)) {
      return _generate((Ref)chapter);
    } else if ((chapter instanceof Table)) {
      return _generate((Table)chapter);
    } else if ((chapter instanceof Todo)) {
      return _generate((Todo)chapter);
    } else if ((chapter instanceof UnorderedList)) {
      return _generate((UnorderedList)chapter);
    } else if ((chapter instanceof Item)) {
      return _generate((Item)chapter);
    } else if ((chapter instanceof TableData)) {
      return _generate((TableData)chapter);
    } else if ((chapter instanceof TableRow)) {
      return _generate((TableRow)chapter);
    } else if ((chapter instanceof TextOrMarkup)) {
      return _generate((TextOrMarkup)chapter);
    } else if ((chapter instanceof TextPart)) {
      return _generate((TextPart)chapter);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        java.util.Arrays.<Object>asList(chapter).toString());
    }
  }
  
  public StringConcatenation generateCode(final EObject code) {
    if ((code instanceof Code)) {
      return _generateCode((Code)code);
    } else if ((code instanceof MarkupInCode)) {
      return _generateCode((MarkupInCode)code);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        java.util.Arrays.<Object>asList(code).toString());
    }
  }
}