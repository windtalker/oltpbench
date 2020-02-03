#!/bin/bash

resultDir=""
if [ -n "$1" ]
then
	resultDir=$1
else
	echo "resultDir is not specified"
	echo "Usages: ./extract_result resultDir [apThreads tpThreads wd]"
	exit 1
fi
apThreads="2 4 8 16 32 64 0"
if [ -n "$2" ]
then
	apThreads=$2
fi
tpThreads="64 128 256 512 1024"
if [ -n "$3" ]
then
	tpThreads=$3
fi
wd=100
if [ -n "$4" ]
then
	wd=$4
fi

rm -rf $resultDir/throughput_ap.txt
rm -rf $resultDir/throughput_tp.txt
rm -rf $resultDir/avg_latency_ap.txt
rm -rf $resultDir/avg_latency_tp.txt
for ap in $apThreads
do 
	for tp in $tpThreads
	do
		if test $(find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_ap\.summary" | wc -c) -ne 0
		then
			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/throughput_ap.txt
			find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_ap\.summary" -exec grep Throughput {} + | awk -F':' '{print $NF}' >> $resultDir/throughput_ap.txt

			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/avg_latency_ap.txt
			find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_ap\.summary" -exec grep "Average Latency" {} + | awk -F':' '{print $NF}' >> $resultDir/avg_latency_ap.txt
		else
			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/throughput_ap.txt
			echo "NA" >> $resultDir/throughput_ap.txt

			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/avg_latency_ap.txt
			echo "NA" >> $resultDir/avg_latency_ap.txt
		fi

		if test $(find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_tp\.summary" | wc -c) -ne 0
		then
			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/throughput_tp.txt
			find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_tp\.summary" -exec grep Throughput {} + | awk -F':' '{print $NF}' >> $resultDir/throughput_tp.txt

			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/avg_latency_tp.txt
			find $resultDir/results/ -name "*ap_${ap}_tp_${tp}_wd_${wd}_tp\.summary" -exec grep "Average Latency" {} + | awk -F':' '{print $NF}' >> $resultDir/avg_latency_tp.txt
		else
			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/throughput_tp.txt
			echo "NA" >> $resultDir/throughput_tp.txt

			echo -e "ap-${ap}-tp-${tp}-wd-${wd}: \c" >> $resultDir/avg_latency_tp.txt
			echo "NA" >> $resultDir/avg_latency_tp.txt
		fi

	done
done
