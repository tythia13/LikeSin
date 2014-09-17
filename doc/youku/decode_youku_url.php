<?php

/*
 * @author:农夫庄园
 * website:http://www.icultivator.com
 * url:http://www.icultivator.com/p/4409.html
 */

//if (isset($_POST['url'])):
    $url = rawurldecode($_POST['url']);
    $pattern = "/http:\/\/v\.youku\.com\/v_show\/id_([\w]+)\.html.*/";
    preg_match($pattern, $url, $arr);
    $vid = XNzA0Njg1Mjc2_ev_1;//$arr[1];
    $yk_api = 'http://v.youku.com/player/getPlayList/VideoIDS/' . $vid;

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $yk_api);
    curl_setopt($ch, CURLOPT_HEADER, false);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.1 Safari/537.11');
    $res = curl_exec($ch);
    curl_close($ch);

    echo $res;

//endif;
?>
