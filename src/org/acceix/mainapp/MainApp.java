/*
 * The MIT License
 *
 * Copyright 2022 Rza Asadov (rza dot asadov at gmail dot com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.acceix.mainapp;




import org.acceix.frontend.crud.loaders.CustomAssetsLoader;
import org.acceix.frontend.crud.loaders.CustomViewsLoader;
import org.acceix.frontend.crud.loaders.DbStoredLoader;
import org.acceix.frontend.crud.loaders.ObjectLoader;
import org.acceix.frontend.crud.loaders.ScriptLoader;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.acceix.frontend.builtin.dbstored.BuildInDbStoredLoader;
import org.acceix.frontend.builtin.functions.BuiltInFunctionsLoader;
import org.acceix.frontend.builtin.objects.BuiltInObjectsLoader;
import org.acceix.frontend.core.accounts.AuthManager;
import org.acceix.frontend.core.NetondoMaintanance;
import org.acceix.frontend.crud.CoreModule;
import org.acceix.frontend.crud.ViewModule;
import org.acceix.frontend.crud.loaders.AdminAssetsLoader;
import org.acceix.frontend.crud.loaders.AdminViewsLoader;
import org.acceix.frontend.crud.loaders.FunctionLoader;
import org.acceix.frontend.files.UploadManager;
import org.acceix.frontend.location.LocationManager;
import org.acceix.ndatabaseclient.DataConnector;
import org.acceix.ide.modules.NCodeAdminAssets;
import org.acceix.ide.modules.NCodeDbStored;
import org.acceix.ide.modules.NCodeFunctions;
import org.acceix.ide.modules.NCodeObjects;
import org.acceix.ide.modules.NCodeScripts;
import org.acceix.ide.modules.NCodeAdminViews;
import org.acceix.ide.modules.NCodeCustomViews;
import org.acceix.ide.modules.NCodeCustomAssets;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 *
 * @author zrid
 */
public class MainApp {

    private static String JRE_PATH = "";
    private static String INSTALL_PATH = "";
    private static String CONFIG_FILE_PATH = "";

    private static String LOGDIR_PATH = "";

    private static final Map<String, Object> ENVS = new HashMap<>();

    public static String getJRE_PATH() {
        return JRE_PATH;
    }

    public static String getINSTALL_PATH() {
        return MainApp.INSTALL_PATH;
    }

    public static void setINSTALL_PATH(String INSTALL_PATH) {
        MainApp.INSTALL_PATH = INSTALL_PATH;
    }

    public static String getLOGDIR_PATH() {
        return LOGDIR_PATH;
    }

    public static Map<String, Object> getENVS() {
        return ENVS;
    }
    
    

    public static void main(String[] args) throws FileNotFoundException {

        try {
            
            LinkedHashMap arguments = ArgumentParser.parseArguments(args);
            CONFIG_FILE_PATH = (String)arguments.get("CONFIG_FILE_PATH");
            
        } catch (ParseException | org.apache.commons.cli.ParseException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Starting main app........");

        setPaths();

        if (!readConfig()) {
            System.exit(0); // Exit if troubles with reading  config file;
        }

        if (System.getProperty("user.name").equals("root")) {
            System.err.println("Running under root prohibited ! Please use another user !");
            System.exit(0);
        }


        // Start web server


            if ((ENVS.get("database_ssh_user") != null) 
                    && (ENVS.get("database_ssh_host") != null)) {
                  connecToVpn();
            }


            FunctionLoader.setGlobalEnvs(ENVS);
            ObjectLoader.setGlobalEnvs(ENVS);
            DbStoredLoader.setGlobalEnvs(ENVS);
            
            new BuiltInObjectsLoader().loadObjects(); // Load builtin objects
            new BuiltInFunctionsLoader().loadFunctions(); // Load builtin functions
            new BuildInDbStoredLoader().loadDbStored(); // Load builtin db stored            
            
            new FunctionLoader().loadAll(ENVS.get("functions_path").toString());
            
            
            new ObjectLoader().loadAll(ENVS.get("objects_path").toString());
            

            new DbStoredLoader().loadAll(ENVS.get("dbstored_path").toString());
            
            new ScriptLoader().loadAll(ENVS.get("scripts_path").toString());
            new AdminViewsLoader().loadAll(ENVS.get("admin_views_path").toString());
            new CustomViewsLoader().loadAll(ENVS.get("custom_views_path").toString());
            new CustomAssetsLoader().loadAll(ENVS.get("custom_web_assets_path").toString());
            new AdminAssetsLoader().loadAll(ENVS.get("admin_web_assets_path").toString());
            
            

        

        WebService mainWebService = new WebService(ENVS);
        
        mainWebService.addNetondoModule(new AuthManager());

        mainWebService.addNetondoModule(new NetondoMaintanance());
        
        mainWebService.addNetondoModule(new NCodeObjects());
        mainWebService.addNetondoModule(new NCodeFunctions());
        mainWebService.addNetondoModule(new NCodeDbStored());
        mainWebService.addNetondoModule(new NCodeScripts());
        mainWebService.addNetondoModule(new NCodeAdminViews());
        mainWebService.addNetondoModule(new NCodeCustomViews()); 
        mainWebService.addNetondoModule(new NCodeCustomAssets()); 
        mainWebService.addNetondoModule(new NCodeAdminAssets()); 
        
        mainWebService.addNetondoModule(new UploadManager());
        mainWebService.addNetondoModule(new LocationManager());

       
        mainWebService.addNetondoModule(new CoreModule());
       
        mainWebService.addNetondoModule(new ViewModule());         
      

        try {
            mainWebService.run();
            System.out.println("AcceIX Platform started.");
        } catch (Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
        /*try {
            String actual = Files.readString(Path.of("/home/zrid/sample.json"));
            
            new DataUtils().readJsonObjectFromString(actual);
            
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    }



    public static boolean readConfig()  {

        try {
            File confFile = new File(CONFIG_FILE_PATH);
            if (!confFile.exists()) {
                System.out.println("ERROR: Could't find config file on path " + CONFIG_FILE_PATH + ", exiting.....");
                return false;
            } else {

                Ini iniConfHandler = new Ini(confFile);

                Section section = iniConfHandler.get("mainsettings");
                section.keySet().forEach((optionKey) -> {
                    
                    
                    
                    String value = section.get(optionKey);
                    
                    if (optionKey.endsWith("_path")) {
                        if(value.endsWith("/")) {
                              value = value.substring(0,value.length() - 1);
                        }
                    }
                    
                    if (value.contains("%home_path%")) {
                        value = value.replaceAll("%home_path%", section.get("home_path"));
                    }
                    
                    if (value.contains("%app%")) {
                        value = value.replaceAll("%app%", section.get("app"));
                    }                    

                    ENVS.put(optionKey, value);
                });
                
                if (ENVS.get("apps_folder_name")==null) ENVS.put("apps_folder_name", "apps");
                
                if (ENVS.get("admin_domain")==null) ENVS.put("admin_domain", "admindomain");
                if (ENVS.get("env_mode")==null) ENVS.put("env_mode", "DEBUG");
                if (ENVS.get("reverse_proxy_use")==null) ENVS.put("reverse_proxy_use", "yes");
                if (ENVS.get("reverse_proxy_ip_header")==null) ENVS.put("reverse_proxy_ip_header", "X-FORWARDED-FOR");

                if (ENVS.get("admin_views_path")==null) ENVS.put("admin_views_path",  ENVS.get("home_path") + "/" + "admin_views");
                if (ENVS.get("admin_web_assets_path")==null) ENVS.put("admin_web_assets_path", ENVS.get("home_path") + "/" + "admin_webassets");
                if (ENVS.get("custom_views_path")==null) ENVS.put("custom_views_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/views");                
                if (ENVS.get("custom_web_assets_path")==null) ENVS.put("custom_web_assets_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/webassets");
                
                if (ENVS.get("objects_path")==null) ENVS.put("objects_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "objects");
                if (ENVS.get("dbstored_path")==null) ENVS.put("dbstored_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "dbstored");
                if (ENVS.get("functions_path")==null) ENVS.put("functions_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "functions");
                if (ENVS.get("upload_dir")==null) ENVS.put("upload_dir", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "uploads");
                if (ENVS.get("documents_path")==null) ENVS.put("documents_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "uploads");
                if (ENVS.get("upload_dir_tmp")==null) ENVS.put("upload_dir_tmp", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "upload_tmp");
                if (ENVS.get("scripts_path")==null) ENVS.put("scripts_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/" + "scripts");
                
                
                if (ENVS.get("max_form_content_size")==null) ENVS.put("max_form_content_size", "10000000");
                if (ENVS.get("max_upload_size")==null) ENVS.put("max_upload_size", "100000000");
                if (ENVS.get("max_form_content_size")==null) ENVS.put("max_form_content_size", "100000000");
                if (ENVS.get("file_size_threshold")==null) ENVS.put("file_size_threshold", "65536");
                
                if (ENVS.get("index_file")==null) ENVS.put("index_file", "index.html");
                if (ENVS.get("wrong_domain_view_file")==null) ENVS.put("wrong_domain_view_file", "/wrong_domain");
                if (ENVS.get("context_path")==null) ENVS.put("context_path", "/");
                if (ENVS.get("api_url")==null) ENVS.put("api_url", "/engine");

                
                
                if (ENVS.get("license_file")==null) ENVS.put("license_file", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/configs/license.npt");
                if (ENVS.get("ssl_keystore_path")==null) ENVS.put("ssl_keystore_path", ENVS.get("home_path") + "/" + ENVS.get("apps_folder_name") + "/" + ENVS.get("app") + "/configs/default_keystore.jks");
                if (ENVS.get("ssl_keystore_password")==null) ENVS.put("ssl_keystore_password", "123456");
                if (ENVS.get("ssl_keymanager_password")==null) ENVS.put("ssl_keymanager_password", "123456");
                
                
                DataConnector dataConnector = new DataConnector(ENVS,"system");
                
                    
                    try (PreparedStatement preparedStatement = dataConnector.getConnection().prepareStatement("delete from npt_settings")) {
                        
                        preparedStatement.executeUpdate();                        
                            
 
                        
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }                 
                
                
                ENVS.forEach((k,v) -> {
                    
                    if (k.equals("database_port")) return;
                    if (k.equals("database_password")) return;
                    if (k.equals("database_user")) return;
                    if (k.equals("database_host")) return;
                    if (k.equals("database_schema")) return;
                    if (k.equals("ssl_keystore_password")) return;
                    if (k.equals("ssl_keymanager_password")) return;
                    if (k.equals("http_port")) return;
                    if (k.equals("database_driver")) return;
                    if (k.equals("admin_password")) return;
                    if (k.equals("admin_user")) return;
                    if (k.equals("admin_domain")) return;

                    
                            String create_sql_cmd = "insert into npt_settings (key_name,key_value) values (?,?)";
                            
                            try (PreparedStatement preparedStatement_for_create = dataConnector.getConnection().prepareStatement(create_sql_cmd)) { 
                                preparedStatement_for_create.setString(1, k);
                                preparedStatement_for_create.setString(2, (String)v);
                                preparedStatement_for_create.executeUpdate();
                            }  catch (ClassNotFoundException | SQLException ex) {
                                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                            }                       
                    
                });
                

                
                return true;

            }

        } catch (IOException ex) {
            System.out.println("ERROR: Unable to read config file, please fix it. Exiting.....");
            return false;

        }

    }

    private static void setPaths() {

        // Get install path from executed jar
        String jarPath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            jarPath = URLDecoder.decode(jarPath, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        jarPath = new File(jarPath).getPath();

        if (INSTALL_PATH.equals("")) {

            INSTALL_PATH = Paths.get(jarPath).toFile().getParent();

            if (INSTALL_PATH.endsWith("build")) {
                INSTALL_PATH = INSTALL_PATH.substring(0, INSTALL_PATH.length() - 6);
            } else if (INSTALL_PATH.endsWith("dist")) {
                INSTALL_PATH = INSTALL_PATH.substring(0, INSTALL_PATH.length() - 5);
            }

        }

        // Set server log path
        LOGDIR_PATH = INSTALL_PATH + File.separator + "logs";

        if (!new File(getLOGDIR_PATH()).exists()) {
            new File(getLOGDIR_PATH()).mkdir();
        }

        // Set JRE Path
        JRE_PATH = INSTALL_PATH + File.separator + "jre";

    }
    
    private static void connecToVpn() {
        
                System.out.print("Trying to establish ssh tunnel to  database....");

                SshTunnelClient sshTunnelClient = new SshTunnelClient();

                sshTunnelClient.setLocalhost("localhost");
                sshTunnelClient.setLocalport((String) ENVS.get("database_port"));
                sshTunnelClient.setUser((String) ENVS.get("database_ssh_user"));
                sshTunnelClient.setRemotehost((String) ENVS.get("database_ssh_host"));
                sshTunnelClient.setRemoteport("3306");
                //sshTunnelClient.setSshKeyFilepath((String)ENVS.get("database_ssh_keyfilepath"));

                sshTunnelClient.start();

                while (!sshTunnelClient.isConnectedToSSH()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                System.out.println("......DONE");        
    }
    

}
