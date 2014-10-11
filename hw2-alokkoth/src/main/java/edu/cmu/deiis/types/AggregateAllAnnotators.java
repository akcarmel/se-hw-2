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
import com.aliasi.util.AbstractExternalizable;

import edu.stanford.nlp.dcoref.CoNLL2011DocumentReader.NamedEntityAnnotation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import abner.Tagger;
public class AggregateAllAnnotators extends JCasAnnotator_ImplBase {
  final public static String annotator_id  = "Abner";
  Tagger nlpba_model;
@Override
public void initialize(UimaContext aContext) throws ResourceInitializationException {
  /* @param UimaContext aContext
   * Initializes the lingpipe NE-chunker, with the model file read from the parameter supplied: "ModelFileName".
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
     * The initialized NE-chunker annotates named entities in the text. 
     * Then the offset begin and offset end (counting only nonzero characters), along with the named entity is added to the CAS.
     */
    FSIterator iter = java_cas.getJFSIndexRepository().getAllIndexedFS(SentenceAnnotation.type);
    SentenceAnnotation sent = (SentenceAnnotation) iter.next();
    String onesent = sent.getSentence();
    String[][] NESet = nlpba_model.getEntities((String) onesent);
    for (String ner : NESet[0])
    {   
       ner = HandleBrackets(ner);
         System.out.println(ner);
        if ( onesent.matches(ner) == false )
        {
           continue;
        }
        else
        {
        int start = onesent.indexOf(ner);
        int end = start + ner.length();
        NEAnnotate ners = new NEAnnotate(java_cas);
        int offset_start = GetNonZeroChars((String) onesent, start);
        int offset_end = GetNonZeroChars((String) onesent,end)-1;
        String phrase = (String) onesent.substring(start, end);
        ners.setBegin(offset_start);
        
        ners.setEnd(offset_end);
        ners.setNamedEntity(phrase);
         
        
        ners.addToIndexes();
        }
        
    }

  }
  public static String HandleBrackets(String string)
  
  {
    
    String string1 = string.replaceAll("\\(", "\\\\(");
    String string2 = string1.replaceAll("\\)","\\\\)");
    return string2;
    
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
