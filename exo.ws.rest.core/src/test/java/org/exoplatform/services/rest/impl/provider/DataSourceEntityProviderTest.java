/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.services.rest.impl.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;

import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.util.ByteArrayDataSource;

public class DataSourceEntityProviderTest extends BaseTest {

  private final DataSourceEntityProvider provider = new DataSourceEntityProvider();

  private DataSource read(byte[] entity) throws Exception {
    return provider.readFrom(DataSource.class, null, null, MediaType.valueOf("application/octet-stream"), null,
                             new ByteArrayInputStream(entity));
  }

  public void testSmallEntityStaysInMemory() throws Exception {
    byte[] entity = "small".getBytes(StandardCharsets.UTF_8);
    DataSource source = read(entity);
    assertTrue(source instanceof ByteArrayDataSource);
    assertEquals("small", new String(source.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
  }

  public void testLargeEntityGoesToAFileOnlyItsOwnerCanAccess() throws Exception {
    ApplicationContextImpl.getCurrent().getProperties().put(RequestHandler.WS_RS_BUFFER_SIZE, "64");
    byte[] entity = new byte[10_000];
    new Random(3).nextBytes(entity);
    DataSource source = read(entity);
    assertTrue(source instanceof FileDataSource);
    File file = ((FileDataSource) source).getFile();
    try {
      assertEquals(10_000, file.length());
      assertTrue(java.util.Arrays.equals(entity, source.getInputStream().readAllBytes()));
      assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file.toPath())));
    } finally {
      Files.deleteIfExists(file.toPath());
    }
  }
}
