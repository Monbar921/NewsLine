import React, {useState, useEffect} from "react";
import Cookies from "js-cookie";

const commentCreator = (URL, method, content) => {
    console.log(URL);
    console.log(content);

    fetch(URL, {
        method: method,
        body: JSON.stringify(content),
        headers: {
            'Authorization': `Bearer ${Cookies.get("access_token")}`,
            Accept: 'application/json',
            'Content-Type': 'application/json'
        },
        mode: "cors"
    })
        .then(response => {
            if(response.status === 200 && method === "POST"){
                 window.location.reload()
            }
        })
        .catch((err) => {
            console.log(err.message);
        });
};

export default commentCreator;

