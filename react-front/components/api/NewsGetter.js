import React, {useEffect} from "react";
import Cookies from "js-cookie";

// const URL="http://resource-server:8080/news/get-all-news";
const URL="/resource-server/news/get-all-news";

export const NewsGetter = (currentPage, newsOnPage, {setPageCount, setData, setIsLoaded}) => {
    useEffect(() => {
        console.log("inside getter")
        setIsLoaded(false)
        const jwt_token = Cookies.get("access_token")
        const authorization = `Bearer ${jwt_token}`;
        const currentPageAsParam = currentPage - 1
        console.log(authorization)
        console.log("page " + currentPageAsParam)
            // if (jwt_token) {
                fetch(URL + `?current-page=${currentPageAsParam}&news-on-page=${newsOnPage}`, {
                    method: 'GET',
                    mode: "cors",
                    headers: {
                        Authorization: authorization,
                        Accept: '*/*',
                        "Content-Type": "application/json",
                    }
                })
                    .then(response => {
                        console.log("recieve")
                        return response.json();
                    })
                    .then(data => {
                        setPageCount(data.message.pages);
                        setData(data.message.data);
                        setIsLoaded(true);
                        console.log(data.message.data)
                    })
                    .catch((err) => {
                        console.log(err.message);
                    });
            // }
    }, [currentPage]);
    // return {listNews};
};

export default NewsGetter;

