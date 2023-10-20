import React, {useEffect, useState} from "react";
import Cookies from "js-cookie";

export default function Image({newsId, imageId}) {

    const [img, setImg] = useState();
    // const imageUrl = `http://resource-server:8080/news/${newsId}/get-image/${imageId}`;
    const imageUrl = `/resource-server/news/${newsId}/get-image/${imageId}`;
    const fetchImage = async () => {
        const res = await fetch(imageUrl, {
            method: 'GET',
            mode: "cors",
            headers: {
                'Authorization': `Bearer ${Cookies.get("access_token")}`,
                Accept: '*/*'
            }
        });

        const imageBlob = await res.blob();
        const imageObjectURL = URL.createObjectURL(imageBlob);
        setImg(imageObjectURL);
    };

    useEffect(() => {
        fetchImage();
    }, [imageId]);


    return (
        <div className="d-flex justify-content-between">
            <div className="row justify-content-start">
                <div className="col-8">
                    <img src={img} className="img-thumbnail" alt="Responsive image"/>
                </div>
            </div>
        </div>
    );
}
