package com.oculus.task2.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oculus.task.IntegrationException;
import com.oculus.task.IntegrationService;
import com.oculus.task2.model.PacketEntity;
import com.oculus.task2.model.PacketKey;
import com.oculus.task2.model.ReportKey;
import com.oculus.task2.repository.PacketReportRepository;
import com.oculus.task2.repository.PacketRepository;

import io.pkts.Pcap;
import io.pkts.packet.MACPacket;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;

@Service
public class PacketIntegrationService implements IntegrationService<ReportKey,PacketEntity> {
		
	@Autowired
	private PacketRepository packetRepository;
	
	@Autowired
	private PacketReportRepository packetReportRepository;
	
	@Value("${oculus.task2.dircontent}")
	private String packetContentDirectory;
	
	
	@Override
	public int processRead(File file, ReportKey reportId, List<PacketEntity> packets) throws IntegrationException {
		Pcap pcap = null;
		InputStream is = null;
		PacketsCount nbPakets = new PacketsCount();
		try {
			is = new FileInputStream(file);
			pcap = Pcap.openStream(is);
			pcap.loop(packet -> {
				 if (packet.hasProtocol(Protocol.TCP) && packet.hasProtocol(Protocol.ETHERNET_II)) {
					 TCPPacket tcp = (TCPPacket) packet.getPacket(Protocol.TCP);
					 if(tcp.getPayload() == null){
						 return true;
					 }
					 nbPakets.addPacket();
					 HTMLParser parser = new HTMLParser(tcp.getPayload());
					 UUID id = UUID.randomUUID();
					 String contentPacketsDirectory = packetContentDirectory
							 + File.separator + FilenameUtils.removeExtension(file.getName());
					 new File(contentPacketsDirectory).mkdir();
					 File fileBodyContent = parser.createFileFromBody(contentPacketsDirectory 
							 + File.separator + id.toString());
					 
					 String dstAdress = tcp.getDestinationIP() + ":" + tcp.getDestinationPort();
					 String srcAdress = tcp.getSourceIP() + ":" + tcp.getSourcePort();
					 
					 MACPacket ip = (MACPacket) packet.getPacket(Protocol.ETHERNET_II);
					 String macDest = ip.getDestinationMacAddress();
					 String macSrc = ip.getSourceMacAddress();
					 PacketKey packetKey = new PacketKey(file.getName(), UUID.randomUUID());
					 PacketEntity packetEntity = new PacketEntity(packetKey, "TCP", dstAdress, 
							 srcAdress, macDest, macSrc,
							 new Date(Instant.ofEpochMilli(packet.getArrivalTime() / 1000).toEpochMilli()),
							 tcp.isPSH(),
							 (fileBodyContent != null)?fileBodyContent.getName():null);
					 packets.add(packetEntity);
				 }
				 return true;
			});
		} catch (IOException e) {
			throw new IntegrationException(e.getMessage());
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nbPakets.getPackets();
	}

	@Override
	@Transactional
	public int processWrite(ReportKey reportId, List<PacketEntity> entities) throws IntegrationException {
		return packetRepository.saveAll(entities).size();
	}

	@Override
	@Transactional
	public int clearLastReport(File file) {
		int packetsDeleted = packetRepository.deleteByKeyFileName(file.getName()).size();
		packetReportRepository.deleteByKeyFileName(file.getName());
		try {
			File dirContent = new File(packetContentDirectory + 
					File.separator + FilenameUtils.removeExtension(file.getName()));
			if(dirContent.exists()){
					FileUtils.deleteDirectory(dirContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packetsDeleted;
	}
	
	
	@Override
	public void checkFile(File file) throws IntegrationException {
		Optional<String> extFile = getExtensionByStringHandling(file.getName());
		if(!extFile.isPresent() || !extFile.get().equals("pcap")){
			throw new IntegrationException("file not present or format error");
		}
	}
	
	private Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}


	private static class PacketsCount {
		Integer packets = 0;
		
		public synchronized void addPacket(){
			packets++;
		}
		
		public Integer getPackets(){
			return packets;
		}
		
	}
}
