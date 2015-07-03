
<?php
$mysql_host = "hashtagdb.ctscqnvckzf1.us-east-1.rds.amazonaws.com:3306";
$mysql_database = "hashtagDB";
$mysql_user = "weipuz";
$mysql_password = "twitterhashtag";
if (!$link = mysql_connect($mysql_host, $mysql_user, $mysql_password)) {
    echo 'Could not connect to mysql';
    exit;
}

if (!mysql_select_db($mysql_database, $link)) {
    echo 'Could not select database';
    exit;
}

	if(empty($_POST)){
		$time = '2012-03-06 17:33:07';
	}else{
		//for($index = 0; $index<=30;$index++){
			$StartTime = $_POST['StartTime'];
			$date = substr($StartTime, 0,2);
			$month = substr($StartTime, 3,2);
			$year = substr($StartTime, 6,4);
			$hour = substr($StartTime, 11,2);
			$minute = substr($StartTime, 14,2) + $index;
			$StartTime_new = $year ."-".$month."-".$date." ".$hour.":".$minute.":"."00";
			
			$EndTime = $_POST['EndTime'];
			$date = substr($EndTime, 0,2);
			$month = substr($EndTime, 3,2);
			$year = substr($EndTime, 6,4);
			$hour = substr($EndTime, 11,2);
			$minute = substr($EndTime, 14,2) + $index;
			$EndTime_new = $year ."-".$month."-".$date." ".$hour.":".$minute.":"."00";

			//----------------------select from table
			$sql    = "select name, sum(value) as total from hashtag60s where time >= '$StartTime_new' 
			and time <= '$EndTime_new' group by name order by total DESC";
			$result = mysql_query($sql, $link);
			//---------------------------------

			if (!$result) {
			    echo "DB Error, could not query the database\n";
			    echo 'MySQL Error: ' . mysql_error();
			    exit;
			}
			
			//load data to file.csv
			$filename = 'file1.csv';
			if(file_exists($filename)){
				unlink($filename);
			}
			
			usleep(100000);
			$fp = fopen('file1.csv', 'w');
			$title_arr = array('name','value');
			fputcsv($fp, $title_arr);
			$count =0;
			
			while ($row = mysql_fetch_assoc($result) ) {
				if($count<10){
						$row_arr = array( $row['name'] ,$row['total']);
						fputcsv($fp, $row_arr);
						$count++;
					
				}				
			}
			$showtime = "Time from ".$StartTime_new ." to ".$EndTime_new;
			
			echo $showtime;
			fclose($fp);	
	};
?>
<?php header('Access-Control-Allow-Origin: *'); ?>


<!DOCTYPE html>
<html lang="en">
	<head>
		<script type="text/javascript" src="jquery-1.11.0.min.js"></script>
		<script type="text/javascript" src="src/DateTimePicker.js"></script>
		
		
		<script src="https://www.google.com/jsapi"></script>
		
		<script src="https://jquery-csv.googlecode.com/files/jquery.csv-0.71.js"></script>
		
		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
		
		<!-- Optional theme -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
		
		<!-- Latest compiled and minified JavaScript -->
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
		<meta charset="utf-8">

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame
		Remove this if you use the .htaccess -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

		<title>twitter_hot_hashtag</title>
		<meta name="description" content="">
		<meta name="author" content="Shijie Li">

		<meta name="viewport" content="width=device-width; initial-scale=1.0">

		<link rel="stylesheet" type="text/css" href="src/DateTimePicker.css" />
	
		
	    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
	  google.setOnLoadCallback(drawChart);
	
	// this has to be a global function
	function drawChart() {
	   // grab the CSV
	   $.get("file1.csv", function(csvString) {
	      // transform the CSV string into a 2-dimensional array
	      var arrayData = $.csv.toArrays(csvString, {onParseValue: $.csv.hooks.castToScalar});
	
	      // this new DataTable object holds all the data
	      var data = new google.visualization.arrayToDataTable(arrayData);
	

	     // set chart options
	     var options = {
	        
	        //vAxis: {title: data.getColumnLabel(0), minValue: data.getColumnRange(0).min, maxValue: data.getColumnRange(0).max},
	        //hAxis: {title: data.getColumnLabel(1), minValue: data.getColumnRange(1).min, maxValue: data.getColumnRange(1).max},
	        //legend: 'none'
	     };
	
	     // create the chart object and draw it
	     var chart = new google.visualization.BarChart(document.getElementById('bar'));
	     chart.draw(data, options);
	  });
	}
    </script>
		
		<style type="text/css">

			#timepicker{
				width:300px;
				height: 300px;
				float: left;
 
			}
			#timeform{
				margin-top: 150px;
			}
						
			p
			{
				margin-left:100px;
			}
		
			input
			{
				margin-left:100px;
			}
			h1{
				text-align: center;
				width:960px;
			}
			#content{
				width:960px;
				
			}
			#bar{
				width:500px;
				height: 450px;
				float: right;
				margin-top:50px; 
			}
			
		</style>
	</head>

	<body>
		<h1>
			Twitter Top 10 hot hashtag
		</h1>
		
		<div id ="content">
			<!-- Time picker -->
			<div id = "timepicker">
				<form action="" method="POST" id = "timeform">
					<p>Start Time : </p>
					<input type="text" name = "StartTime" data-field="datetime" readonly>
				
					<div id="dtBox"></div>
				
					<script type="text/javascript">
					
						$(document).ready(function()
						{
							$("#dtBox").DateTimePicker();
						});
					
					</script>
					
					<p>End Time : </p>
					<input type="text" name = "EndTime" data-field="datetime" readonly>
				
					<div id="dtBox"></div>
				
					<script type="text/javascript">
					
						$(document).ready(function()
						{
							$("#dtBox").DateTimePicker();
						});
					
					</script>
					<input class="submit" name="submit" type="submit" value="Update" >
				</form>
				
			</div>

			<div id = "bar">
			</div>			
		</div>		
	</body>
</html>
