package edu.cmu.deiis.types;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;


public class MyJCasConsumer extends CasConsumer_ImplBase {
  private BufferedWriter bw;
  @Override
  public void collectionProcessComplete(org.apache.uima.util.ProcessTrace arg0) throws ResourceProcessException ,IOException {
    
  };
  public void initialize() throws ResourceInitializationException {
    /*
     * Creates a BufferedWriter to write the output file. Reads the file name from the parameter "outputfilename".
     */
    try {
      bw = new BufferedWriter(new FileWriter((String) getConfigParameterValue("outputfilename")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    super.initialize();
  }
  
  public void processCas(CAS CasConsumer) throws ResourceProcessException {
    
    /* @param CAS CasConsumer
     * Loads both type systems (Sentence and Annotation) from CAS. Then corresponding to each sentence id, outputs the begin and end, and the identified named-entity.
     * Writes the id,begin,end,named entity in the output file via the created buffered reader.
     */
    JCas jcas_consumer = null;
    HashSet<String> lines = new HashSet<String>();
    try {
      jcas_consumer = CasConsumer.getJCas();
    } catch (CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String id = ((SentenceAnnotation)  jcas_consumer.getJFSIndexRepository().getAllIndexedFS(SentenceAnnotation.type).get()).getSentid(); 
    FSIterator it = jcas_consumer.getJFSIndexRepository().getAllIndexedFS(Token.type);
    while (it.hasNext()) {
      Token ner = ((Token) it.get());
      
      try {
        
        String new_ner_string=id + "|" + ner.getBegin() + " " + ner.getEnd() + "|" + ner.getNerstring();
        
        if (!lines.contains(new_ner_string))
        {
         bw.write(new_ner_string+'\n');
         lines.add(new_ner_string);
         
        }
       
          
       
        
         
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      it.next();
    }
    try{
    bw.flush();  
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
    }


}
