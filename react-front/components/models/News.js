import React from "react";
import {Link} from "react-router-dom";
import CommentIcon from '@mui/icons-material/Comment';

function BoldText({children}) {
    return (
        <span style={{fontWeight: 'bold'}}>{children}</span>
    );
}

const News = ({news, drawImages, handleRemove, showButtons}) => {
    const canUserModifyNews = news.canUserModifyNews
    return (<div>
            <p><BoldText>{news.title}</BoldText></p>
            <p>{news.date}</p>
            <p>{news.text}</p>
            {
                news.imagesID && news.imagesID.length > 0 && drawImages(news.id, news.imagesID)
            }
            {showButtons &&
                <div className="d-flex justify-content-between">
                    <div className="row justify-content-start">
                        <div className="col">

                            <Link to={`/newsline/news/${news.id}`}>
                                <button>
                                    <CommentIcon/>
                                </button>
                            </Link>
                        </div>
                        <div className="col"/>
                        <div className="col"/>
                        <div className="col">

                            <Link to={`/newsline/news/${news.id}/edit-news`}>
                                <button type="button" disabled={!canUserModifyNews}>
                                    Edit
                                </button>
                            </Link>
                        </div>
                        <div className="col">
                            <button type="button" onClick={() => handleRemove(news.id)} disabled={!canUserModifyNews}>
                                Remove
                            </button>
                        </div>
                    </div>
                </div>
            }
        </div>
    );
}

export default News;
