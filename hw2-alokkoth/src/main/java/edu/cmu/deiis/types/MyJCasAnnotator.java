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

public class MyJCasAnnotator extends JCasAnnotator_ImplBase {
  Chunker chunker;
  public static String ANNOTATOR_ID = "Lingpipe"; /** CAS PROCESSOR ID which tells the annotator ID. **/
@Override
public void initialize(UimaContext aContext) throws ResourceInitializationException {
  /* @param UimaContext aContext
   * Initializes the lingpipe NE-chunker, with the model file read from the parameter supplied: "ModelFileName".
   */
  try
  {
    chunker = (Chunker) AbstractExternalizable.readResourceObject((String)aContext.getConfigParameterValue("ModelFileName"));
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
    Set<Chunk> NESet = chunker.chunk((String) sent.getSentence()).chunkSet();
    for (Chunk c : NESet)
    {
        NEAnnotate ner = new NEAnnotate(java_cas);
        int start = c.start();
        int end = c.end();
        
        int offset_start = GetNonZeroChars((String) sent.getSentence(),start);
        int offset_end = GetNonZeroChars((String) sent.getSentence(),end)-1;
        Token token = new Token(java_cas,offset_start,offset_end);
        String phrase = (String) sent.getSentence().substring(start, end);
     
          token.setCasProcessorId(ANNOTATOR_ID);
          token.setNerstring(phrase);
          token.addToIndexes();

      
       
    }

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
