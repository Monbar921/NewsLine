import React, {useState} from "react";
import Paginator from "../components/common/Paginator";

const NewslinePage = ({handleGet, handleRemove, drawImages}) => {

    const [newsOnPage, setNewsOnPage] = useState(Number(localStorage.getItem('newsOnPage')));
    const handleClick = number => {
        localStorage.setItem('newsOnPage', number.target.value);
        setNewsOnPage(Number(localStorage.getItem('newsOnPage')));
        window.location.replace("/newsline");
    }

    return (
        <div>
            <Paginator handleGet={handleGet} itemsPerPage={newsOnPage} handleRemove={handleRemove}
                       drawImages={drawImages}/>
            <a>Новостей на странице</a>
            <form>
                <select onChange={e => handleClick(e)}>
                    <option selected={newsOnPage === 1}>1</option>
                    <option selected={newsOnPage === 2}>2</option>
                    <option selected={newsOnPage === 5}>5</option>
                </select>
            </form>
            <a href="/">На главную</a>
        </div>
    )
        ;
};

export default NewslinePage;
