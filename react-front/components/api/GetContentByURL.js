import React, {useState, useEffect} from "react";
import Cookies from "js-cookie";

const GetContentByURL = (URL, {setter, setDownloaded}) => {
    console.log(URL);

    useEffect(() => {
        fetch(URL, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${Cookies.get("access_token")}`,
                Accept: 'application/json'
            }, mode: 'cors'
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                setter(data.message.data);
                setDownloaded(true);
                console.log(data)
            })
            .catch((err) => {
                console.log(err.message);
            });
    }, []);

};

export default GetContentByURL;

