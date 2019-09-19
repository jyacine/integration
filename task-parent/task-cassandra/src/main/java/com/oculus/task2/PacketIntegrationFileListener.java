package com.oculus.task2;

import org.springframework.stereotype.Component;

import com.oculus.task.IntegrationFileListener;
import com.oculus.task2.model.PacketEntity;
import com.oculus.task2.model.ReportKey;

@Component
public class PacketIntegrationFileListener extends IntegrationFileListener<ReportKey, PacketEntity> {

}
