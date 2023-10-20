import React, {useState} from "react";

const AddNewsPage = ({handler}) => {
    const [message, setMessage] = useState("");
    console.log("add news");

    const handleSubmit = (e) => {
        e.preventDefault();

        handler(e, setMessage);
    }

    return (
        <div>
            <form onSubmit={handleSubmit} enctype="multipart/form-data">
                <table>
                    <td>Заголовок:</td>
                    <tr>
                        <input type="text" name="title" size="50" required/>
                    </tr>
                    <td>Дата:</td>
                    <tr>
                        <input type="date" name="date" required/>
                    </tr>
                    <td>Содержимое:</td>
                    <tr>
                        <textarea name="text" className="form-control" cols="50" rows="10" required></textarea>
                    </tr>
                    <tr>
                        <input type="file" name="image" accept="image/*"/>
                    </tr>
                    <br/>
                    <tr>
                        <button type="submit">Add news</button>
                    </tr>
                    {message.length > 0
                        &&
                        <h2>{message}</h2>
                    }
                </table>
            </form>
            <br/>
            <a href="/">На главную</a>
            <br/>
            <a href="/newsline">Лента новостей</a>
        </div>

    );
}

export default AddNewsPage;
