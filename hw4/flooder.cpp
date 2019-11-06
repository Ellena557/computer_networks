#include <stdio.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>    
#include <sys/socket.h>
#include <net/if.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pcap.h>
#include <stdlib.h>
#include <netinet/ip.h>
#include <netinet/if_ether.h>
#include <netinet/ether.h>
#include <iostream>
#include <time.h>
#include <cstring>
#include <random>

using namespace std;

u_char srcIP[4];
u_char srcMAC[6];


struct packetARP {
	u_char dstMac0[6];				// here we have dstMac0 = dstMac but in general case it isn't like this
	u_char srcMac0[6];
	u_short etherType;				// for ARP: 0x0806

	u_short htype;					// hardware type
	u_short ptype;					// protocol type
	u_char hlen;					// hardware address length
	u_char plen;					// protocol address length
	u_short operation;				// operation code
	u_char srcMac[6];				// sender MAC address
	u_char srcIp[4];				// sender IP address
	u_char dstMac[6];				// target MAC address
	u_char dstIp[4];				// target IP address
};


void printField(int type, u_char* info) {
	/*
	0 - MAC
	1 - IP
	*/

	if (type == 0) {
		for (int i = 0; i < 6; i++) {
			printf("%02x", info[i]);
			if (i < 5) {
				printf(":");
			}
		}		
	}

	if (type == 1) {
		for (int i = 0; i < 4; i++) {
			printf("%d", info[i]);
			if (i < 3) {
				printf(".");
			}
		}
	}	
}


/* This function finds:
- device MAC 
- device IP
by the name of the device
*/
void fillTheFields(char* device, u_char* mac, u_char* ip) {
	int s = socket(PF_INET, SOCK_DGRAM, 0);

	struct ifreq buffer;
	memset(&buffer, 0x00, sizeof(buffer));
	buffer.ifr_addr.sa_family = AF_INET;
	strcpy(buffer.ifr_name, device);

	ioctl(s, SIOCGIFHWADDR, &buffer);
	memcpy(mac, buffer.ifr_hwaddr.sa_data, 6);

	ioctl(s, SIOCGIFADDR, &buffer);
	memcpy(ip, &(((struct sockaddr_in*) & (buffer.ifr_addr)) -> sin_addr), 4);

	close(s);
}


void fillMACfield(char* device, u_char* mac) {
	int s = socket(PF_INET, SOCK_DGRAM, 0);

	struct ifreq buffer;
	memset(&buffer, 0x00, sizeof(buffer));
	buffer.ifr_addr.sa_family = AF_INET;
	strcpy(buffer.ifr_name, device);

	ioctl(s, SIOCGIFHWADDR, &buffer);
	memcpy(mac, buffer.ifr_hwaddr.sa_data, 6);

	close(s);
}


void fillIPfield(char* device, u_char* ip) {
	int s = socket(PF_INET, SOCK_DGRAM, 0);

	struct ifreq buffer;
	memset(&buffer, 0x00, sizeof(buffer));
	buffer.ifr_addr.sa_family = AF_INET;
	strcpy(buffer.ifr_name, device);

	ioctl(s, SIOCGIFADDR, &buffer);
	memcpy(ip, &(((struct sockaddr_in*) & (buffer.ifr_addr)) -> sin_addr), 4);

	close(s);
}


void getAllDevices() {
	char errbuf[PCAP_ERRBUF_SIZE];
	pcap_if_t* alldevs;										// Item in a list of interfaces, used by pcap_findalldevs(): next, name, description, addresses, flags
	int status = pcap_findalldevs(&alldevs, errbuf);		// this is a list of devices that can be opened with pcap_open_live()

	cout << "START PRINTING ALL DEVICES" << endl;
	cout << "device name | device IP | device MAC " << endl;
	for (pcap_if_t* d = alldevs; d != NULL; d = d -> next) {
		printf("%s:", d -> name);
		cout << " | ";
		for (pcap_addr_t* a = d -> addresses; a != NULL; a = a->next) {
			if (a->addr->sa_family == AF_INET) {
				u_char devIP[4];
				u_char devMAC[6];
				fillTheFields(d->name, devMAC, devIP);
				printField(1, devIP);
				cout << " | ";
				printField(0, devMAC);
				//cout << "device IP: ";
				//printf(" %s", inet_ntoa(((struct sockaddr_in*)a->addr)->sin_addr));
			}
		}
		cout << endl;
	}
	cout << "END PRINTING ALL DEVICES" << endl;
}


void sendARPpacket(u_char* randMAC, u_char* randIP, pcap_t* mainHandler) {
	struct packetARP myPacket;
	memcpy(myPacket.dstMac0, srcMAC, 6);
	memcpy(myPacket.srcMac0, randMAC, 6);
	myPacket.etherType = htons(0x0806);		// ARP 
	myPacket.htype = 1;						// ethernet
	myPacket.ptype = htons(0x0800);			// IPv4
	myPacket.hlen = 6;						// ethernet mac addresses have length 6
	myPacket.plen = 4;						// IPv4 addresses have length 4
	myPacket.operation = htons(0x0002);		// REPLY
	memcpy(myPacket.srcMac, randMAC, 6);
	memcpy(myPacket.srcIp, randIP, 4);
	memcpy(myPacket.dstMac, srcMAC, 6);
	memcpy(myPacket.dstIp, srcIP, 4);

	/*
	it allows to send a raw packet to the network
	p = interface that will be used to send the packet
	buf = contains the data of the packet to send
	The return value is 0 if the packet is succesfully sent, -1 otherwise.
	*/
	int result = pcap_sendpacket(mainHandler, (u_char*)&myPacket, sizeof(myPacket));

	if (result == 0) {
		cout << "The reply has been successfully sent!" << endl;
	} else {
		cout << "An error occured while sending a reply!" << endl;
	}
}


void getOneRandomDevice(pcap_t* mainHandler) {
	char errbuf[PCAP_ERRBUF_SIZE];
	pcap_if_t* alldevs;
	int status = pcap_findalldevs(&alldevs, errbuf);

	//cout << "RANDOM DEVICE START" << endl;
	int numDevs = 0;
	for (pcap_if_t* d = alldevs; d != NULL; d = d->next) {
		numDevs++;
	}
	cout << "All number of devices: " << numDevs << endl;
	int randNum = rand() % numDevs;
	cout << "Random device number: " << randNum << endl;

	u_char randMAC[6];
	u_char randIP[4];

	int curNum = 0;
	for (pcap_if_t* d = alldevs; d != NULL; d = d->next) {
		if (curNum = randNum) {
			fillTheFields(d->name, randMAC, randIP);
			break;
		}
		curNum++;
	}

	cout << "Random device MAC: ";
	printField(0, randMAC);
	cout << endl;
	cout << "Random device IP: ";
	printField(1, randIP);
	cout << endl;

	//cout << "RANDOM DEVICE END" << endl;
	sendARPpacket(randMAC, randIP, mainHandler);
}


int main(int argc, char* argv[])
{
	int numPacketsToSend;			// required number of ARP packets
	cout << "Print how many ARP replies you want to send: " << endl;
	cin >> numPacketsToSend;
	cout << endl;
	char errbuf[PCAP_ERRBUF_SIZE];

	// define our device (it returns the first valid device in the system)
	char* dev = pcap_lookupdev(errbuf);

	fillTheFields(dev, srcMAC, srcIP);		
	//fillMACfield(dev, srcMAC);
	//fillIPfield(dev, srcIP);
	cout << "My device: " << dev << endl;
	cout << "My MAC: ";
	printField(0, srcMAC);
	cout << endl;
	cout << "My IP: ";
	printField(1, srcIP);
	cout << endl;
	cout << endl;

	// Uncomment this to see all possible devices
	//getAllDevices();
	
	for (int i = 0; i < numPacketsToSend; i++) {
		cout << "Sending ARP reply number " << i + 1 << endl;

		/*
		This function is used to obtain a packet capture descriptor to look at packets on the network
		device = string that specifies the network device to open
		snaplen = the maximum number of bytes to capture
		promisc = if the interface is to be put into promiscuous mode
		to_ms = the read timeout in milliseconds
		errbuf = used to return error or warning text
		*/
		pcap_t* handle = pcap_open_live(dev, BUFSIZ, 1, 2000, errbuf);
		getOneRandomDevice(handle);

		/*
		This function closes the files associated with p and deallocates resources.
		*/
		pcap_close(handle);

		cout << "------------------------------" << endl;
		cout << endl;
	}

	return 0;
}
