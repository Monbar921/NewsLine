import {useEffect} from "react";
import React from "react";

const CLIENT_ID = "messaging-client";
const SECRET = "secret";
const redirectUri = 'http://localhost/authorized';
// const redirectUri = 'http://front:80/authorized';
// const authorizationEndpoint = 'http://localhost:9000/oauth2/authorize';
const authorizationEndpoint = '/auth-server/oauth2/authorize';
const scope = "read";

function authorization() {
    const params = new URLSearchParams({
        response_type: 'code',
        client_id: CLIENT_ID,
        redirect_uri: redirectUri,
        scope: scope
    });
    console.log(`${authorizationEndpoint}?${params}`)
    window.location.href = `${authorizationEndpoint}?${params}`;
    //     window.location.href = `/auth-server/login`;
}

export default authorization;

// curl -X POST \
//     -H "Cache-Control: no-cache" \
//     -H "Content-Type: application/x-www-form-urlencoded" \
//     "http://localhost:9000/oauth2/token" \
//     -d "client_id=messaging-client" \
//     -d "client_secret=secret" \
//     -d "code=xXZzWBgzXj7ZsEdRkv8KaeB7yjgiz8E4JjKX23vUUQTyOCIaeDyu5pzzI7YvclmGMGvNf_c1fAgQU9FED7J60OvI6QqltPgtNO4LmXozPpHZq1d6p1whYYEFw0wQbVuc" \
//     -d "redirect_uri=http://127.0.0.1:3000/authorized" \
//     -d "grant_type=authorization_code"


// curl -v -X POST \
//     -H "Cache-Control: no-cache" \
//     -H "Content-Type: application/x-www-form-urlencoded" \
//     "http://localhost:9000/oauth2/authorize" \
//     -d "client_id=messaging-client" \
//     -d "client_secret=secret" \
//     -d "redirect_uri=http://127.0.0.1:3000/authorized" \
//     -d "response_type=code" \
//     -d "scope=message:read" \
//     -d "_csrf=fda7e64c-3013-41c3-8473-c4a7fe7e2242" \
//     --cookie "XSRF-TOKEN=fda7e64c-3013-41c3-8473-c4a7fe7e2242"


// local
// http://localhost:9000/oauth2/authorize?response_type=code&client_id=messaging-client&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Fauthorized&scope=read


// 127
// http://localhost:9000/oauth2/authorize?response_type=code&client_id=messaging-client&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Fauthorized&scope=read

// http://localhost:9000/oauth2/authorize?response_type=code&client_id=messaging-client&redirect_uri=http://127.0.0.1:3000/authorized&scope=read