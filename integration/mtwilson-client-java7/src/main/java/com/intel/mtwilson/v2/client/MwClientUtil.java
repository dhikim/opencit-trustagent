/*
 * Copyright (C) 2014 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.v2.client;

import com.intel.dcsg.cpg.crypto.CryptographyException;
import com.intel.dcsg.cpg.crypto.RsaCredentialX509;
import com.intel.dcsg.cpg.crypto.RsaUtil;
import com.intel.dcsg.cpg.crypto.SimpleKeystore;
import com.intel.dcsg.cpg.io.FileResource;
import com.intel.dcsg.cpg.io.Resource;
import com.intel.dcsg.cpg.tls.policy.TlsPolicy;
import com.intel.dcsg.cpg.tls.policy.TlsUtil;
import com.intel.dcsg.cpg.tls.policy.impl.InsecureTlsPolicy;
import com.intel.mtwilson.My;
import com.intel.mtwilson.api.ApiException;
import com.intel.mtwilson.api.ClientException;
import com.intel.mtwilson.attestation.client.jaxrs.CaCertificates;
import com.intel.mtwilson.user.management.client.jaxrs.RegisterUsers;
import com.intel.mtwilson.user.management.rest.v2.model.RegisterUserWithCertificate;
import com.intel.mtwilson.user.management.rest.v2.model.User;
import com.intel.mtwilson.user.management.rest.v2.model.UserLoginCertificate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jbuhacoff
 */
public class MwClientUtil {
    
    private static final Logger log = LoggerFactory.getLogger(MwClientUtil.class);
    
    private static SimpleKeystore createUserKeystoreInResource(Resource resource, String username, String password) throws CryptographyException, IOException {
        try {
            // create the keystore and a new credential
            SimpleKeystore keystore = new SimpleKeystore(resource, password); // KeyManagementException
            KeyPair keypair = RsaUtil.generateRsaKeyPair(RsaUtil.MINIMUM_RSA_KEY_SIZE); // NoSuchAlgorithmException
            X509Certificate certificate = RsaUtil.generateX509Certificate(/*"CN="+*/username, keypair, RsaUtil.DEFAULT_RSA_KEY_EXPIRES_DAYS); // GeneralSecurityException
            keystore.addKeyPairX509(keypair.getPrivate(), certificate, username, password); // KeyManagementException
            keystore.save(); // KeyStoreException, IOException, CertificateException        
            return keystore;
        } 
        catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            throw new CryptographyException("Cannot create keystore", e);
        }
    }
    
    public static SimpleKeystore createUserInDirectoryV2(File directory, String username, String password, URL server, String comments) throws IOException, ApiException, CryptographyException, ClientException {
        if( username.contains("..") || username.contains(File.separator) || username.contains(" ") ) { throw new IllegalArgumentException("Username must not include path-forming characters"); }
        File keystoreFile = new File(directory.getAbsoluteFile() + File.separator + username + ".jks");
        FileResource resource = new FileResource(keystoreFile);
        return createUserInResourceV2(resource, username, password, server, comments);
    }

    public static SimpleKeystore createUserInResourceV2(Resource resource, String username, String password, 
            URL server, String comments) throws IOException, ApiException, CryptographyException, ClientException {
        return createUserInResourceV2(resource, username, password, server, new InsecureTlsPolicy(), comments, null);
    }

    public static SimpleKeystore createUserInResourceV2(Resource resource, String username, String password, 
            URL server, TlsPolicy tlsPolicy, String comments, Locale locale) throws IOException, ApiException, CryptographyException, ClientException {
        return createUserInResourceV2(resource, username, password, server, tlsPolicy, comments, locale, "TLS");
    }

    public static SimpleKeystore createUserInResourceV2(Resource resource, String username, String password, 
            URL server, TlsPolicy tlsPolicy, String comments, Locale locale, String tlsProtocol) throws IOException, ApiException, CryptographyException, ClientException {
        
        URL baseUrl = new URL(server.getProtocol() + "://" + server.getAuthority());
        SimpleKeystore keystore = createUserKeystoreInResource(resource, username, password);
        
        log.debug("URL Protocol: {}", baseUrl.getProtocol());        
        if( "https".equals(baseUrl.getProtocol()) ) {
            TlsUtil.addSslCertificatesToKeystore(keystore, baseUrl, tlsProtocol); //CryptographyException, IOException            
        }

        try {
            String[] aliases = keystore.aliases();
            for(String alias : aliases) {
                log.debug("Certificate: "+keystore.getX509Certificate(alias).getSubjectX500Principal().getName());
            }
        }
        catch(KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | CertificateEncodingException e) {
            log.debug("cannot display keystore: "+e.toString());
        }
        
        try {
            
            RegisterUsers client = new RegisterUsers(My.configuration().getClientProperties());
            
            RegisterUserWithCertificate rpcUserWithCert = new RegisterUserWithCertificate();            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setLocale(locale);
            newUser.setComment(comments);
            
            RsaCredentialX509 rsaCredential = keystore.getRsaCredentialX509(username, password);            
            UserLoginCertificate userLoginCertificate = new UserLoginCertificate();
            userLoginCertificate.setCertificate(rsaCredential.getCertificate().getEncoded());
            userLoginCertificate.setComment(comments);
            
            rpcUserWithCert.setUser(newUser);
            rpcUserWithCert.setUserLoginCertificate(userLoginCertificate);
            boolean result = client.registerUserWithCertificate(rpcUserWithCert);
            if( !result ) {
                throw new IllegalStateException("Failed to register user with certificate");
            }
        } catch (Exception ex) {
            log.error("Error during creation of user.", ex);
        }
        
        try {
            
            CaCertificates certClient = new CaCertificates(My.configuration().getClientProperties());
            X509Certificate rootCertificate = certClient.retrieveCaCertificate("root");
            X509Certificate samlCertificate = certClient.retrieveCaCertificate("saml");
            X509Certificate privacyCertificate = certClient.retrieveCaCertificate("privacy");
            
            log.debug("Adding CA Certificate with alias {} from server {}", rootCertificate.getSubjectX500Principal().getName(), server.getHost());
            keystore.addTrustedCaCertificate(rootCertificate, rootCertificate.getSubjectX500Principal().getName());

            log.debug("Adding Privacy CA Certificate with alias {} from server {}", privacyCertificate.getSubjectX500Principal().getName(), server.getHost());
            keystore.addTrustedCaCertificate(privacyCertificate, privacyCertificate.getSubjectX500Principal().getName());
            
            if (samlCertificate.getBasicConstraints() == -1) { // -1 indicates the cert is not a CA cert
                log.debug("Adding SAML Certificate with alias {} from server {}", samlCertificate.getSubjectX500Principal().getName(), server.getHost());
                keystore.addTrustedSamlCertificate(samlCertificate, samlCertificate.getSubjectX500Principal().getName());

            } else {
                log.debug("Adding SAML Certificate as CA cert with alias {} from server {}", samlCertificate.getSubjectX500Principal().getName(), server.getHost());
                keystore.addTrustedCaCertificate(samlCertificate, samlCertificate.getSubjectX500Principal().getName());                
            }
        } catch (Exception ex) {
            log.error("Error during retrieval of certificates for writing to the key store.", ex);
        }
                                
        try {
            keystore.save();
            return keystore;
        }
        catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new CryptographyException("Cannot save keystore to resource: "+e.toString(), e);
        }
    }
    
}
