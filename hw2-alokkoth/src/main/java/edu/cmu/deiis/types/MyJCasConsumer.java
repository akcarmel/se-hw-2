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
    /* param arg0
     * This function is called after the loop of calling processcas ends. In this function the output of the two annotators
     * that is gathered at the end of processCas can be finally combined.
     * * Writes the id,begin,end,named entity in the output file via the created Print Reader.
     */
    
    Set<String> keySet = ling.keySet();
    Set<String> keySet2 = abz.keySet();
    HashSet<String> lines = new HashSet<String>();
    
    /*** ATTEMPT 5 **/
  
    for(String id: keySet)
    {
      ArrayList<String> l = ling.get(id);
      for(String outline: l)
      {
      pw.write(id + "|"+ outline+'\n'); 
      lines.add(outline);
      }
    }
    for(String id: keySet2)
    {
      ArrayList<String> l = abz.get(id);
      for(String outline: l)
      {
        if (!lines.contains(outline))
        {
          String o1 = outline.split("|")[1];
          ArrayList<String> l2 = abz.get(id);
          for (String outline2: l2)
          {
            String o2 = outline2.split("|")[1];
            if (!o2.contains(o1))
            {
              pw.write(id + "|"+ outline+'\n'); 
              break;
            }
            
          }
          
     
      }
      }
    }
    pw.flush();
    
    /***
     * 
     * ATTEMPT 1
     for (String str: lingpiped)
     {
       
      if(abnered.contains(str))
       {
       
         pw.write(str+'\n');
       }

     }

     pw.flush();
   
     ***/
    
    /***
     * 
     * ATTEMPT 2**/
    /***
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
          String o1 = outline.split("|")[1];
          String o2 = outline2.split("|")[1];
          
          if ( o1.contains(o2) || o2.contains(o1))
          {
            pw.write(id + "|"+ outline+'\n'); 
          }
         
        }
      }
    }
   
    pw.flush();
      ***/
    /***
     * 
     * ATTEMPT 3*/
    /***
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
        
            pw.write(id + "|"+ outline+'\n'); 
         
      }
    }
     
    pw.flush();
    ***/
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
     * Loads both type systems (Sentence and Token) from CAS. Then corresponding to each sentence id, outputs the begin and end, and the identified named-entity.
     * The type system also has annotator ID, which tells us which annotator the token came from.
     
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
              
      /**
       * Adding the tokens to appropriate hashmap/hashset depending on which tokenizer it came from.
       */      
      
      if ( annotator_id.equals("Lingpipe"))
      {
        lingpiped.add(with_id_string);
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
        abnered.add(with_id_string);
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
