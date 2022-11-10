package com.bussin.SpringBack.security;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AwsCognitoRSAKeyProvider implements RSAKeyProvider {

    private final URL awsKidStoreUrl;
    private final JwkProvider provider;

    /**
     * Constructs an AWS Cognito RSA Key Provider
     *
     * @param awsCognitoRegion The region to construct the provider in
     * @param awsUserPoolsId   The user pool to construct the provider for
     */
    public AwsCognitoRSAKeyProvider(final String awsCognitoRegion, final String awsUserPoolsId) {
        String url = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", awsCognitoRegion, awsUserPoolsId);
        try {
            awsKidStoreUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Invalid URL provided, URL=%s", url));
        }
        provider = new JwkProviderBuilder(awsKidStoreUrl).build();
    }


    /**
     * Gets the public key used to verify JWTs
     *
     * @param kid The KID
     * @return The public key for decryption
     */
    @Override
    public RSAPublicKey getPublicKeyById(final String kid) {
        try {
            return (RSAPublicKey) provider.get(kid).getPublicKey();
        } catch (JwkException e) {
            throw new RuntimeException(String.format("Failed to get JWT kid=%s from aws_kid_store_url=%s", kid, awsKidStoreUrl));
        }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public String getPrivateKeyId() {
        return null;
    }
}