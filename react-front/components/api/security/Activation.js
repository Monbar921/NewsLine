import React, {useState} from "react";

export const Activation = ({url, getter, authorization}) => {
    // let activate_url = "http://auth-server:9000";
    let activate_url = "/auth-server/oauth2/activate";
    console.log(url)
    const [isGet, setIsGet] = useState(false);
    const urlAsArray = url.split("/");


        activate_url = activate_url + "/" + urlAsArray[2]

    console.log(activate_url)

    getter(activate_url, setIsGet);
    if (isGet) {
        authorization()
    }

    return (
        <div>Activation...</div>);
}