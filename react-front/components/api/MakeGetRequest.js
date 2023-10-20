import React, {useState, useEffect} from "react";
import Cookies from "js-cookie";

const makeGetRequest = (URL, setter) => {
    console.log(URL);

    fetch(URL, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${Cookies.get("access_token")}`
        }, mode: 'cors'
    })
        .then(response => {
            setter(true);
        })
        .catch((err) => {
            console.log(err.message);
        });


};

export default makeGetRequest;

