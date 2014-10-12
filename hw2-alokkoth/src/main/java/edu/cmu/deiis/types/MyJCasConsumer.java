package edu.cmu.deiis.types;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
  private PrintWriter pw=null;
  public HashSet<String> lingpiped = new HashSet<String>();
  public HashSet<String> abnered = new HashSet<String>();
  public HashMap<String,ArrayList<String>> ling = new HashMap<String,ArrayList<String>>();
  public HashMap<String,ArrayList<String>> abz = new HashMap<String,ArrayList<String>>();
  
  
  @Override
  public void collectionProcessComplete(org.apache.uima.util.ProcessTrace arg0) throws ResourceProcessException ,IOException 
  {
    
    Set<String> keySet = ling.keySet();
    /***
     for (String str: lingpiped)
     {
       
      if(abnered.contains(str))
       {
       
         pw.write(str+'\n');
       }

     }

     pw.flush();
     **/
    for(String id: keySet)
    {

      ArrayList<String> l = ling.get(id);
      if ( abz.get(id) == null)
      {
        continue;
      }
      ArrayList<String> l2 = abz.get(id);
      for(String outline: l)
      {
        for(String outline2: l2)
        {
          
          if ( outline.contains(outline2) || outline2.contains(outline))
          {
            pw.write(id + "|"+ outline+'\n'); 
          }
         
        }
      }
    }
    pw.flush();
}
    
    
    
  
  public void initialize() throws ResourceInitializationException {
    /*
     * Creates a BufferedWriter to write the output file. Reads the file name from the parameter "outputfilename".
     */
    try {
      pw = new PrintWriter(new FileWriter((String) getConfigParameterValue("outputfilename")));
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
      
      String new_ner_string=ner.getBegin() + " " + ner.getEnd() + "|" + ner.getNerstring();
      String annotator_id = ner.getCasProcessorId();
      String with_id_string = id + "|" + ner.getBegin() + " " + ner.getEnd() + "|" + ner.getNerstring();
              
              
      
      if ( annotator_id.equals("Lingpipe"))
      {
        lingpiped.add(new_ner_string);
        if ( ling.get(id) == null )
        {
          ArrayList<String> l = new ArrayList<String>();
          l.add(new_ner_string);
          ling.put(id,l);
        }
        else
        {
          
          ArrayList<String> l = ling.get(id);
          l.add(new_ner_string);
          ling.put(id, l);
        }
          
     
  
        
        }
      if ( annotator_id.equals("Abner"))
      {
        abnered.add(new_ner_string);
        if ( abz.get(id) == null )
        {
          ArrayList<String> l = new ArrayList<String>();
          l.add(new_ner_string);
          abz.put(id,l);
        }
        else
        {
          
          ArrayList<String> l = abz.get(id);
          l.add(new_ner_string);
          abz.put(id, l);
        }
        
      }
      
      it.next();
    }
   
    
    }


}
