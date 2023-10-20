import React, {useState} from "react";
import {Route, Switch, useLocation} from "react-router-dom";
import HomePage from "../../pages/HomePage.js";
import NewslinePage from "../../pages/NewslinePage.js";
import AddNewsPage from "../../pages/AddNewsPage.js";
import EditNewsPage from "../../pages/EditNewsPage.js";
import NewsGetter from "../api/NewsGetter";
import GetContentByURL from "../api/GetContentByURL";
import DeleteContentByURL from "../api/DeleteContentByURL";
import Image from "../api/Image";
import SignUp from "../../pages/SignUpPage";
import SignInPage from "../../pages/SignInPage";
import {ProtectedRoute} from "../security/ProtectedRoute";
import {registration} from "../api/security/Registration";
import {Login} from "../api/security/Login";
import authorization from "../api/security/Authorization";
import NewsPage from "../../pages/NewsPage";
import commentCreator from "../api/CommentCreator";
import {newsCreator} from "../api/NewsCreator";
import makeGetRequest from "../api/MakeGetRequest";
import {Activation} from "../api/security/Activation";

const Service = () => {
    const location = useLocation()
    const params = new URLSearchParams(location.search)


    const textFromStorage = localStorage.getItem('newsOnPage');
    if (textFromStorage === null) {
        localStorage.setItem('newsOnPage', "1");
    }

    const handleGetNews = (currentPage, itemsPerPage, {setPageCount, setData, setIsLoaded}) => {
        NewsGetter(currentPage, itemsPerPage, {setPageCount, setData, setIsLoaded});
    }

    const handleOneNewsGet = (id, setter, setDownloaded) => {
        // const url = `http://resource-server:8080/news/${id}/get-news`;
        const url = `/resource-server/news/${id}/get-news`;
        console.log(url)
        GetContentByURL(url, {setter, setDownloaded});
    }

    const handleAddNews = (formBody, setMessage) => {
        // const url = `http://resource-server:8080/news/save-news`;
        const url = `/resource-server/news/save-news`;
        newsCreator(url, 'POST', formBody, setMessage);
    }
    const handleUpdate = (id, formBody, setMessage) => {
        // const url = `http://resource-server:8080/news/${id}/update-news`;
        const url = `/resource-server/news/${id}/update-news`;
        console.log(url);
        console.log(formBody);
        newsCreator(url, 'PUT', formBody, setMessage);
    }

    const handleRemoveNews = (id) => {
        // const URL = `http://resource-server:8080/news/${id}/delete-news`
        const URL = `/resource-server/news/${id}/delete-news`
        DeleteContentByURL(URL);
    }


    const drawImages = (newsId, imagesID) => {
        return <div>
            {imagesID.map((id) => (
                <Image newsId={newsId} imageId={id} key={id}/>
            ))}
        </div>;
    }
    const handleGetComments = (newsId, setter, setDownloaded) => {
        // const url = `http://resource-server:8080/news/${newsId}/comments/get-comments`;
        const url = `/resource-server/news/${newsId}/comments/get-comments`;
        console.log(url)
        GetContentByURL(url, {setter, setDownloaded});
    }
    const handlePostComment = (newsId, content) => {
        // const URL = `http://resource-server:8080/news/${newsId}/comments/save-comment`;
        const URL = `/resource-server/news/${newsId}/comments/save-comment`;
        commentCreator(URL, "POST", content)
    }

    const handleUpdateComment = (newsId, commentId, content) => {
        // const URL = `http://resource-server:8080/news/${newsId}/comments/update-comment/${commentId}`;
        const URL = `/resource-server/news/${newsId}/comments/update-comment/${commentId}`;
        commentCreator(URL, "PUT", content)
    }

    const handleRemoveComment = (newsId, commentId) => {
        // const URL = `http://resource-server:8080/news/${newsId}/comments/delete-comment/${commentId}`;
        const URL = `/resource-server/news/${newsId}/comments/delete-comment/${commentId}`;
        console.log(URL)
        DeleteContentByURL(URL);
    }


    const handleRegistration = (body) => {
        registration(body);
    }

    const handleLogin = (event) => {
        console.log("login")
        // Login(event);
        authorization();

    }

    const getRequest = (URL, setter) => {
        makeGetRequest(URL, setter)
    }

    const closeSession = (setter) => {
        // const URL = "http://auth-server:9000/logout";
        const URL = "/auth-server/logout";
        getRequest(URL, setter)
    }


    return (

        <Switch>
            <Route exact path="/">
                <ProtectedRoute closeSession={closeSession}>
                    <HomePage/>
                </ProtectedRoute>
            </Route>

            <Route exact path="/signup">
                <SignUp handler={handleRegistration}/>
            </Route>

            <Route path="/activation/**">
                <Activation url={location.pathname} getter={getRequest} authorization={authorization}/>
            </Route>

            <Route path="/authorized">
                <Login code={params.get("code")}/>
            </Route>

            <Route exact path="/signin">
                {/*<div>login</div>*/}
                <SignInPage handler={handleLogin}/>
            </Route>

            <Route exact path="/newsline/news/add-news">
                <ProtectedRoute closeSession={closeSession}>
                    <AddNewsPage handler={handleAddNews}/>
                </ProtectedRoute>
            </Route>
            <Route exact path="/newsline/news/:newsId">
                <ProtectedRoute closeSession={closeSession}>
                    <NewsPage getNews={handleOneNewsGet} getComments={handleGetComments} drawImages={drawImages}
                              saveComment={handlePostComment}
                              updateComment={handleUpdateComment}
                              removeComment={handleRemoveComment}/>
                </ProtectedRoute>

            </Route>
            <Route exact path="/newsline/news/:newsId/edit-news">
                <ProtectedRoute closeSession={closeSession}>
                    <EditNewsPage getNews={handleOneNewsGet} handleUpdate={handleUpdate} drawImages={drawImages}
                    />
                </ProtectedRoute>

            </Route>
            <Route path="/newsline">
                <ProtectedRoute closeSession={closeSession}>
                    <NewslinePage handleGet={handleGetNews} handleRemove={handleRemoveNews} drawImages={drawImages}/>
                </ProtectedRoute>
            </Route>
        </Switch>

    )
};

export default Service;