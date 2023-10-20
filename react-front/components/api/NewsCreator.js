import React, {useState} from "react";
import Cookies from "js-cookie";
const SAVE_IMAGE_URL = "/resource-server/news/";
// const SAVE_IMAGE_URL = "http://resource-server:8080/news/";

export function newsCreator(url, method, formBody, setMessage) {
    console.log(formBody)
    const body = {
        title: formBody.target.title.value,
        date: formBody.target.date.value,
        text: formBody.target.text.value
    };
    const response = fetch(url, {
        method: method,
        body: JSON.stringify(body),
        headers: {
            'Authorization': `Bearer ${Cookies.get("access_token")}`,
            Accept: 'application/json',
            'Content-Type': 'application/json'
        },
        mode: "cors"
    }).then(response => {
        return response.json();
    })
        .then(data => {
            console.log(data)
            if (data.httpStatus === 200) {
                setMessage("Successful!");
                const images = formBody.target.image.files;
                if (images && images.length !== 0) {
                    const formData = new FormData();
                    formData.append("file", images[0]);
                    fetch(SAVE_IMAGE_URL + data.message.id + "/save-image", {
                        method: 'POST',
                        body: formData,
                        headers: {
                            'Authorization': `Bearer ${Cookies.get("access_token")}`,
                        },
                        mode: "cors"
                    })
                }
            } else {
                setMessage("Try again!");
            }
        })
        .catch((err) => {
            console.log(err.message);
        });
}


