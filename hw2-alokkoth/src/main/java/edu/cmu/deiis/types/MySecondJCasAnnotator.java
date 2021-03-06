package edu.cmu.deiis.types;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.coref.Matcher;
import com.aliasi.util.AbstractExternalizable;

import edu.stanford.nlp.dcoref.CoNLL2011DocumentReader.NamedEntityAnnotation;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import abner.Tagger;
public class MySecondJCasAnnotator extends JCasAnnotator_ImplBase {
  final public static String ANNOTATOR_ID  = "Abner"; /** INORDER TO SET THE CAS PROCESSOR ID **/
  Tagger nlpba_model;
@Override
public void initialize(UimaContext aContext) throws ResourceInitializationException {
  /* @param UimaContext aContext
   * Initializes and loads the NLPBA Model for tagging.
   */
  try
  {
    nlpba_model = new Tagger();
    }
  catch(Exception e)
  {
    e.printStackTrace();
  }
  // TODO Auto-generated method stub
  super.initialize(aContext);
}
  @Override
  public void process(JCas java_cas) throws AnalysisEngineProcessException {
    /* @param JCas java_cas
     * The initialized NER annotates named entities in the text. 
     * Then the offset begin and offset end (counting only nonzero characters), along with the named entity is added to the CAS.
     */
    FSIterator iter = java_cas.getJFSIndexRepository().getAllIndexedFS(SentenceAnnotation.type);
    SentenceAnnotation sent = (SentenceAnnotation) iter.next();
    String onesent = sent.getSentence();
    String[][] NESet = nlpba_model.getEntities((String) onesent);
    for (String ner : NESet[0])
    {   
      Pattern com = Pattern.compile(Pattern.quote(ner));
      java.util.regex.Matcher mat = com.matcher(onesent);
      while(mat.find()){
       
        int start = onesent.indexOf(ner);
        int end = start + ner.length();
        int offset_start = GetNonZeroChars((String) onesent, start);
        int offset_end = GetNonZeroChars((String) onesent,end)-1;
       
        Token token = new Token(java_cas,offset_start,offset_end);
      
        
        
      
        String phrase = (String) sent.getSentence().substring(start, end);
        
        
       
        if(phrase.length() >= 8)  
       
        {
        
      
        token.setCasProcessorId(ANNOTATOR_ID);
        token.setNerstring(phrase);
        token.addToIndexes();
        }
       
        
        /***
        token.setCasProcessorId(ANNOTATOR_ID);
        token.setNerstring(phrase);
        token.addToIndexes();
        
        ***/
        
      
        
        
    }
    }
  }
  public static String MakeItRegExpable(String string)
  /** @param string
   *  Inorder to make searching inside the text easy, so that regexes can be passed to matcher without worries.
   */
  {
    string = string.replaceAll(" \\( ", "\\(");
    string = string.replaceAll(" \\) ", "\\)");
    string = string.replaceAll(" \\. ", "\\.");
    string = string.replaceAll(" : ", ":");
    string = string.replaceAll(" , ", ",");
    /** String string1 = string.replaceAll("\\(", "\\\\(");
    String string2 = string1.replaceAll("\\)","\\\\)");
    **/
    return string;
    
  }
  public static int GetNonZeroChars(String given_text,int index)
  {
    /* @param String given_text,int index
     * Counts the number of non-zero whitespace characters in the string.
     */
    int num_chars = 0;
    int i;
    for(i=0; i<index; i++)
    {
      if(given_text.charAt(i) != ' ')
        num_chars = num_chars+1;
    }
    return num_chars;
  }

}
