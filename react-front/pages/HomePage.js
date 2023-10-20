import React from "react";
import Cookies from "js-cookie";
// const DOC_URL = "http://localhost:8080/swagger-ui/index.html"
const DOC_URL = "/docs/";
const HomePage = () => {

    console.log(Cookies.get("is_superuser"))

    return (
        <React.Fragment>
            <div>
                <h1>Добро пожаловать на ленту новостей!</h1>
                <h2>Выберите, что вы хотите сделать:</h2>
                <ul>
                    <li><a href="/newsline/">Смотреть новости</a></li>
                    <li><a href="/newsline/news/add-news">Добавить новость</a></li>
                    {Cookies.get("is_superuser") === "true" && <li><a href={DOC_URL}>Документация</a></li>}
                </ul>

            </div>
        </React.Fragment>
    )
        ;
};

export default HomePage;
