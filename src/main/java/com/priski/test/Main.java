package com.priski.test;


import com.sforce.soap.metadata.*;

import com.sforce.soap.partner.PartnerConnection;

import com.sforce.ws.ConnectionException;

import com.sforce.ws.ConnectorConfig;

 

public class Main {

 

  static final String USERNAME = "thegreenpegion@gmail.com";

  static final String PASSWORD = "priski007tqbEDHfGpe9T7Rdj0BRtvVB66";

  static PartnerConnection connection;

 

  public static void main(String[] args) throws ConnectionException {

   

    ConnectorConfig partnerConfig = new ConnectorConfig();

    ConnectorConfig metadataConfig = new ConnectorConfig();

     

    partnerConfig.setUsername(USERNAME);

    partnerConfig.setPassword(PASSWORD);
    
    partnerConfig.setAuthEndpoint("https://ap2.salesforce.com/services/Soap/u/30.0/00D280000011dc4");

    //partnerConfig.setTraceMessage(true);

     

    @SuppressWarnings("unused")

    PartnerConnection partnerConnection = new PartnerConnection(partnerConfig);

     

    // shove the partner's session id into the metadata configuration then connect

    metadataConfig.setSessionId(partnerConnection.getSessionHeader().getSessionId());
    
    metadataConfig.setUsername(USERNAME);

    metadataConfig.setPassword(PASSWORD);
    
    metadataConfig.setServiceEndpoint("https://ap2.salesforce.com/services/Soap/u/30.0/00D280000011dc4");

    MetadataConnection metadataConnection = com.sforce.soap.metadata.Connector.newConnection(metadataConfig);

   

    // create a new custom object

    String objectName = "WSCCustomObject";

    String displayName = "WSC Custom Object";

     

    CustomObject co = new CustomObject();

    co.setFullName(objectName+"__c");

    co.setDeploymentStatus(DeploymentStatus.Deployed);

    co.setDescription("Created by the WSC using the Metadata API");

    co.setLabel(displayName);

    co.setPluralLabel(displayName+"s");

    co.setSharingModel(SharingModel.ReadWrite);

    co.setEnableActivities(true);

     

    // create the text id field

    CustomField field = new CustomField();

    field.setType(FieldType.Text);

    field.setDescription("The custom object identifier field");

    field.setLabel(displayName);

    field.setFullName(objectName+"__c");

    // add the field to the custom object

    co.setNameField(field);

 

    try {

      // submit the custom object to salesforce

      AsyncResult[] ars = metadataConnection.create(new CustomObject[] { co });

      if (ars == null) {

          System.out.println("The object was not created successfully");

          return;

      }

       

      String createdObjectId = ars[0].getId();

      String[] ids = new String[] {createdObjectId};

      boolean done = false;

      long waitTimeMilliSecs = 1000;

      AsyncResult[] arsStatus = null;

       

      /**

       * After the create() call completes, we must poll the results

       * of the checkStatus() call until it indicates that the create

       * operation is completed.

       */ 

      while (!done) {

          arsStatus = metadataConnection.checkStatus(ids);

          if (arsStatus == null) {

              System.out.println("The object status cannot be retrieved");

              return;

          }

          done = arsStatus[0].isDone();

          if (arsStatus[0].getStatusCode() != null )  {

              System.out.println("Error status code: "+arsStatus[0].getStatusCode());

              System.out.println("Error message: "+arsStatus[0].getMessage());

          }

          Thread.sleep(waitTimeMilliSecs);

          // double the wait time for the next iteration 

          waitTimeMilliSecs *= 2;

          System.out.println("The object state is "+arsStatus[0].getState());

      }

       

      System.out.println("The ID for the created object is "+arsStatus[0].getId());

    }

    catch (Exception ex) {

        System.out.println("\nFailed to create object, error message was: \n" +ex.getMessage());

    }

   

  }

     

	}