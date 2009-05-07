package org.pentaho.pms.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.pentaho.commons.metadata.mqleditor.MqlDomain;
import org.pentaho.commons.metadata.mqleditor.MqlQuery;
import org.pentaho.commons.metadata.mqleditor.editor.service.CWMStartup;
import org.pentaho.commons.metadata.mqleditor.editor.service.MQLEditorService;
import org.pentaho.commons.metadata.mqleditor.editor.service.impl.MQLEditorServiceDeligate;
import org.pentaho.metadata.repository.FileBasedMetadataDomainRepository;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.ui.xul.XulServiceCallback;

public class MQLEditorServiceImpl implements MQLEditorService{

  MQLEditorServiceDeligate deligate;

  public MQLEditorServiceImpl(SchemaMeta meta){
    
    deligate = new MQLEditorServiceDeligate(meta);

    // this is normally provided by PentahoSystem or the metadata editor.
    FileBasedMetadataDomainRepository repo = new FileBasedMetadataDomainRepository();
    repo.setDomainFolder("src/org/pentaho/commons/metadata/mqleditor/sampleMql/thinmodels");
    deligate.initializeThinMetadataDomains(repo);
  }

  public void getDomainByName(String name, XulServiceCallback<MqlDomain> callback) {
    callback.success(deligate.getDomainByName(name));
  }

  public void getMetadataDomains(XulServiceCallback<List<MqlDomain>> callback) {
    callback.success(deligate.getMetadataDomains());
  }

  public void saveQuery(MqlQuery model, XulServiceCallback<String> callback) {
    callback.success(deligate.saveQuery(model));
  }

  public void serializeModel(MqlQuery query, XulServiceCallback<String> callback) {
    callback.success(deligate.serializeModel(query));
  }

  public void getPreviewData(MqlQuery query, int page, int limit, XulServiceCallback<String[][]> callback) {
    callback.success(deligate.getPreviewData(query.getMqlStr(), page, limit));
  }

  public void deserializeModel(String serializedQuery, XulServiceCallback<MqlQuery> callback) {
    callback.success(deligate.deserializeModel(serializedQuery));
  }

  public void refreshMetadataDomains(XulServiceCallback<List<MqlDomain>> callback) {
    callback.success(deligate.refreshMetadataDomains());
    
  }
  
  
  
}
