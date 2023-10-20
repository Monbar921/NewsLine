import React, {useState} from 'react';
import News from "../models/News";
import {Pagination} from "@mui/material";

const Paginator = ({itemsPerPage, handleGet, handleRemove, drawImages}) => {
    const [isLoaded, setIsLoaded] = useState(false);
    const [pageCount, setPageCount] = useState(1);
    const [data, setData] = useState([]);

    const [currentPage, setCurrentPage] = useState(1);
    handleGet(currentPage, itemsPerPage, {setPageCount, setData, setIsLoaded});

    const handlePageChange = (selectedObject) => {
        setCurrentPage(parseInt(selectedObject.target.innerText));

        handleGet(currentPage, itemsPerPage, {setPageCount, setData, setIsLoaded});
        console.log("a2")
        console.log(data)
    };

    return (
        <div>
            <div>
                {isLoaded ? (
                    data.map((news, index) => {
                        return (
                            <div key={index}>
                                <a>=====================================================</a>
                                <News news={news} drawImages={drawImages} handleRemove={handleRemove} showButtons={true}/>
                            </div>
                        );
                    })
                ) : (
                    <div></div>
                )}

                {isLoaded ? (
                    <Pagination count={pageCount} page={currentPage} onChange={handlePageChange} hideNextButton={true}
                                hidePrevButton={true}/>
                ) : (
                    <div>Nothing to display</div>
                )}
            </div>
        </div>
    );
}
export default Paginator;
