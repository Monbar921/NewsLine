import React, {useState} from "react";
import {useLocation, useParams} from "react-router-dom";


const EditNewsPage = ({getNews, handleUpdate, drawImages}) => {
    console.log("edit news");
    const { newsId } = useParams();
    console.log(newsId)
    const [isChanged, setIsChanged] = useState(false);
    const [updateMessage, setUpdateMessage] = useState("");
    const [news, setNews] = useState({});
    const [downloadedNews, setDownloaded] = useState(false);
    getNews(newsId, setNews, setDownloaded);
    const handleSubmit = (e) => {
        e.preventDefault();
        console.log(e.target);

        handleUpdate(newsId, e, setUpdateMessage);
        setIsChanged(false);
    }

    return (
        <div>
            {downloadedNews &&
                <div className="w-50">
                    <form onSubmit={handleSubmit} encType="multipart/form-data" onChange={() => {
                        setIsChanged(true)
                    }}>
                        <table>
                            <tbody>
                            <tr>Заголовок:</tr>
                            <tr>
                                <input type="text" name="title" pattern="^\S+$" size="50" required
                                       defaultValue={news.title}/>
                            </tr>
                            <tr>Дата:</tr>
                            <tr>
                                <input type="date" name="date" required defaultValue={news.date}/>
                            </tr>
                            <tr>Содержимое:</tr>

                            <tr>
                            <textarea name="text" className="form-control" cols="50" rows="10" required
                                      defaultValue={news.text}></textarea>
                            </tr>

                            <tr>Картинки:</tr>
                            <tr>
                                {
                                    news.imagesID && news.imagesID.length > 0 && drawImages(news.id, news.imagesID)
                                }
                            </tr>
                            <tr>
                                <input type="file" name="image" accept="image/*"/>
                            </tr>
                            <tr>
                                <button disabled={!isChanged} type="submit">Edit news</button>
                            </tr>
                            {updateMessage.length > 0
                                &&
                                <h2>{updateMessage}</h2>
                            }
                            </tbody>
                        </table>
                    </form>
                    <br/>
                    <a href="/">На главную</a>
                    <br/>
                    <a href="/newsline">Смотреть новости</a>
                </div>
            }
        </div>

    );
}

export default EditNewsPage;
