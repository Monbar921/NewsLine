import {useHistory} from "react-router-dom";
import React, {useEffect, useState} from 'react';
import Cookies from 'js-cookie';
import {removeCookies, updateAccessToken} from "../api/security/Login";

const LOGIN_PAGE = "/signin";
// const LOGOUT_PAGE = "http://localhost:9000/logout";

export const ProtectedRoute = ({children, closeSession}) => {
    const [shouldCloseSession, setShouldCloseSession] = useState(false);
    const is_logged = Cookies.get('is_logged');

    console.log(Cookies.get('access_token'))

    if (is_logged === "true") {
        let access_token = Cookies.get('access_token');
        if (access_token === undefined || access_token === "undefined") {
            updateAccessToken().then(() => {
                access_token = Cookies.get('access_token');
                if (access_token === undefined || access_token === "undefined") {
                    removeCookies();
                    window.location.replace(LOGIN_PAGE);
                }
            });
            Cookies.set('is_logged', "false")
        }
    } else {
        removeCookies();
        window.location.replace(LOGIN_PAGE);
    }

    const handleClick = () => {
        removeCookies();
        closeSession(setShouldCloseSession);
        // window.location.replace(LOGOUT_PAGE);
        if (shouldCloseSession === true) {
            setShouldCloseSession(false);
            console.log("clear session")
            sessionStorage.clear();
            window.location.replace(LOGIN_PAGE);
        }
    }

    return <div>
        {is_logged === "true" &&
            <div>
                <div className="d-flex justify-content-end">
                    <button onClick={handleClick}>logout
                    </button>
                </div>
                {children}
            </div>
        }
    </div>;
};

