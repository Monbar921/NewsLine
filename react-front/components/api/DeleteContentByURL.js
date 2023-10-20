import React, {useState} from "react";
import Cookies from "js-cookie";


const DeleteContentByURL = (URL) => {
    fetch(URL, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${Cookies.get("access_token")}`,
        },
        mode: "cors"
    }).then(response => {
        window.location.reload()
        // console.log(response)
        // if (response.status === 200) {
        //     window.location.reload()
        // }
    });
}

export default DeleteContentByURL;