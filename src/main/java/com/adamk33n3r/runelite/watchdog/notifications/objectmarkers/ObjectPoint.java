package com.adamk33n3r.runelite.watchdog.notifications.objectmarkers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectPoint
{
    private int id = -1;
    private String name;
    private int regionId;
    private int regionX;
    private int regionY;
    private int plane;
}
