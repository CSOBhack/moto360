<?php


/**
 * Created by PhpStorm.
 * User: milan
 * Date: 21.03.15
 * Time: 13:59
 */

$events = getEvents( );

function getEvents(  ) {
  if ( $_GET['hackers'] === 'no' ) {
    return array();
  }
  $e = array();
  $i = 1;
  while ( true ) {
    $new = 0;
    $time = time();
    $get = '?happened_before='.date('c',$time);
    $get .= '&happened_after='.date('c',$time - (int)$_GET['time']);
    $get .= '&offset=0';
    $get .= '&per_page=50';
    $get .= '&page='.$i;

    $events = curl('traffic.json'.$get);
    //print_r($events);
    foreach ( $events['events'] as $event ) {

      if (!isset($e[$event["event_id"]])) {
        $e[$event["event_id"]] = $event;
        $new++;
      }
    }

    if ($new === 0 || $i > 3 ) {
      break;
    } else {
      $i++;
    }
  }
  return $e;
}

$actors = getActors( );

function getActors(  ) {
  $actors =  curl('actors.json');
  return $actors['actors'];
}

$nodes = getNodes( );

function getNodes(  ) {
  $nodes =  curl('nodes.json');
  return $nodes['nodes'];
}

function curl($enpoint){
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, 'http://csob-hackathon.herokuapp.com:80/api/v1/'. $enpoint);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
  $result = curl_exec($ch);

  curl_close($ch);

  $result = json_decode($result,TRUE);
  return $result['_embedded'];
}


const MAP_WIDTH = 1000;
const MAP_HEIGHT = 446;
function convertLat($lat) {
  return round( ((-1 * $lat) + 90) * (MAP_HEIGHT / 180) ,0);
}

function convertLon($lon) {
  return round( ($lon + 180) * (MAP_WIDTH / 360) ,0);
}


//print_r($nodes); die();

?>
<!doctype html>
<html lang="cs">
<head>

  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Network | Images</title>

  <style type="text/css">
    body {
      font: 10pt arial;
    }
    #mynetwork {
      width: 1400px;
      height: 600px;
      border: 1px solid lightgray;
    }
  </style>

  <script type="text/javascript" src="vis/dist/vis.js"></script>
  <link href="vis/dist/vis.css" rel="stylesheet" type="text/css" />

  <script type="text/javascript">
    var nodes = null;
    var edges = null;
    var network = null;

    var DIR = 'img/refresh-cl/';
    var LENGTH_MAIN = 150;
    var LENGTH_SUB = 20;


    // Called when the Visualization API is loaded.
    function draw() {
      // Create a data table with nodes.
      nodes = [];

      // Create a data table with links.
      edges = [];
      //label: '1 to 3', labelAlignment:'line-above', fontFill:'green'
      <?php

  if ( $_GET['hackers'] != 'no' ) {
        foreach ($actors as $actor) {

          $image = "img/admin.png";
          if ( isset($actor["type"]) && $actor["type"] == "hacker" ) {
            $image = "img/skull.png";
          }
          echo "nodes.push({id: 'actor_".$actor['id']."', label: '".str_replace("'"," ",$actor['name'])."', image: '".$image."', shape: 'image'});\n";
        }
  }
      foreach ($nodes as $node) {

        $size = round($node["active_users"] / 100000, 0);
        $size = pow($size,10);
        //echo "nodes.push({id: '".$node['id']."', label: '".$node['venue_name']."', image: DIR + 'Network-Pipe-icon.png', shape: 'image'});";
        echo "nodes.push({
          id: '".$node['id']."',
          label: '".$node['venue_name']."',
          value: ".$size.",
          shape: 'dot'".
          /*,
          x:".convertLat($node["venue_lat"]).",
          y:".convertLon($node["venue_long"])."*/
       " });";

        if ( isset($node["_embedded"]["layers"]) ) {
          foreach ( $node["_embedded"]["layers"] as $layer ){

            $ratio = $layer['current_robustness'] / $layer['max_robustness'];
            $color = 'grey';
            if ($ratio > 0.8) {
              $color = 'green';
            } elseif ($ratio > 0.6)  {
              $color = 'yellow';
            } elseif ($ratio > 0.4)  {
              $color = 'orange';
            } elseif ($ratio > 0.2)  {
              $color = 'red';
            } elseif ($ratio == 0)  {
              $color = 'black';
            }
            $robustness = ' ' . $layer['current_robustness'] .'/' . $layer['max_robustness'];

            if ( $_GET['ok'] == 0 && $ratio > 0.8)
              continue;

            echo "nodes.push({
              id: '".$node['id'].'_'.$layer['id']."',
              label: '".$layer['name'].$robustness."',
              color: '".$color."',
              shape: 'dot',
              value:1
            });";
            echo "edges.push({from: '".($node['id'].'_'.$layer['id'])."', to: ".$node['id'].", length: LENGTH_SUB});";

          }
        }


        if ( isset($node['parent_id']) && $node['parent_id'] > 1 ) {
          echo "edges.push({from: ".$node['id'].", to: ".$node['parent_id'].", length: 2*LENGTH_MAIN});";
        }
      }


      $connections = array();
      foreach ($events as $event) {

        $to = 0;
        if ( isset($event["_embedded"]["node"]["id"]) ) {
          $to = $event["_embedded"]["node"]["id"];
        } elseif( isset($event["_embedded"]["node"]["id"]) && isset( $event["_embedded"]["system"]["id"] ) )  {
          $to = $event["_embedded"]["node"]["id"] . '_' . $event["_embedded"]["system"]["id"];
        }
        if ( $to ) {
          if ( !isset($connections["actor_".$event["_embedded"]['actor']['id']][$to]) ) {
            $connections["actor_".$event["_embedded"]['actor']['id']][$to] = 1;


            echo "edges.push({from: 'actor_".$event["_embedded"]['actor']['id']."', to: ".$to.", length: 4*LENGTH_MAIN});\n";
          }
        }
      }

      ?>
      // create a network
      var container = document.getElementById('mynetwork');
      var data = {
        nodes: nodes,
        edges: edges
      };
      var options = {
        stabilize: false   // stabilize positions before displaying
      };
      network = new vis.Network(container, data, options);
    }
  </script>
</head>

<body onload="draw()">

<a href="?ok=1&hackers=no">Show all services</a>
<a href="?ok=0&time=900">Show attacks</a>

<div id="mynetwork"></div>

</body>
</html>
