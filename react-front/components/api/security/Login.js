import Cookies from 'js-cookie';
import jwt_decode from "jwt-decode";
import React from "react";

// const tokenURL = 'http://localhost:9000/oauth2/token';
const tokenURL = '/auth-server/oauth2/token';
const ID = "messaging-client";
const SECRET = "secret";
const REDIRECT_URI = 'http://localhost/authorized';
const ADMIN_ROLE = 'admin';
export const Login = ({code}) => {
    console.log("login")
    const params = new URLSearchParams({
        client_id: ID,
        client_secret: SECRET,
        grant_type: 'authorization_code',
        code: code,
        redirect_uri: REDIRECT_URI
    });

    const response = fetch(tokenURL, {
        method: 'POST',
        body: params,
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).then(response => {
        return response.json();
    }).then((data) => {
        handleLogin(data);
    }).catch(exc => {
        console.log(exc);
        removeCookies();
    });


    return (
        <div>login</div>);
}

const handleLogin = (data) => {
    updateCookies(data);
    window.location.replace("/");
}

const handleRefreshToken = (data) => {
    updateCookies(data);
}

const updateCookies = (data) => {
    console.log(data);

    const expires_in = new Date(new Date().getTime() + data.expires_in * 1000);

    console.log(expires_in)

    const access_token_decoded = jwt_decode(data.access_token)

    console.log(access_token_decoded)

    Cookies.set('access_token', data.access_token, {expires: expires_in});
    Cookies.set('refresh_token', data.refresh_token);
    Cookies.set('is_logged', "true");
    Cookies.set('is_superuser', access_token_decoded["is_superuser"]);
    Cookies.set('email', access_token_decoded["email"]);

    console.log(Cookies.get("is_superuser"))
    console.log(Cookies.get("is_logged"))
    console.log(Cookies.get("access_token"))
}

export const removeCookies = () => {
    console.log("remove")
    Cookies.remove("access_token");
    Cookies.remove("refresh_token");
    Cookies.remove("is_superuser");
    Cookies.remove("email");
    Cookies.set('is_logged', "false");
}


export const updateAccessToken = async () => {
    const response = await fetch(tokenURL, {
        method: 'POST',
        body: new URLSearchParams({
            'grant_type': 'refresh_token',
            'client_id': `${ID}`,
            'client_secret': `${SECRET}`,
            'refresh_token': `${Cookies.get('refresh_token')}`
        }),
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).then(response => {
        return response.json();
    }).then((data) => {
        console.log("update");
        handleRefreshToken(data);
    }).catch(exc => {
        console.log(exc);
        removeCookies();
    });
}

