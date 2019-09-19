package com.oculus.task1;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.oculus.task.IntegrationFileListener;

@Component
public class CellIntegrationFileListener extends IntegrationFileListener<UUID, String> {

}
