/*
 * Copyright (c) 2025 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;

import net.bytebuddy.ByteBuddy;
import java.sql.DriverManager;
public class ByteBuddyMockHandlerTest {
    @Test
    public void bootstrap_module_throws_exception(){
        // Mock classes needed to create ModuleSystemFound instance.
        ByteBuddy byteBuddy = mock(ByteBuddy.class);
        SubclassLoader loader = mock(SubclassLoader.class);

        ModuleHandler moduleHandler = ModuleHandler.make(byteBuddy, loader); // Should return an instance of ModuleSystemFound.
        Class<?> source = Integer.class;
        Class<?> target = Integer.class;
        // Forcing conditions so that needsExport/needsRead are true.
        ModuleHandler spyHandler = spy(moduleHandler);
        doReturn(false).when(spyHandler).canRead(any(), any());
        doReturn(false).when(spyHandler).isExported(any(), any());

        // Because Integer is loaded by the bootstrap loader, source.getClassLoader() should be null.
        assertNull(source.getClassLoader());
        MockitoException exception = assertThrows(MockitoException.class,
            () -> spyHandler.adjustModuleGraph(source, target, true, true));
            assertTrue(exception.getMessage().contains("Cannot adjust module graph"));
    }

    @Test
    public void throws_exception_if_bytebuddy_fails(){
        // Mock classes needed to create instance of ModuleSystemFound.
        ByteBuddy byteBuddy = mock(ByteBuddy.class);
        SubclassLoader loader = mock(SubclassLoader.class);
        ModuleHandler moduleHandler = ModuleHandler.make(byteBuddy, loader); // Should return an instance of ModuleSystemFound.

        Class<?> source = DriverManager.class; // source.GetParent().getClassLoader() will return null.
        Class<?> target = Mockito.class;
        // Forcing conditions so that needsExport/needsRead are true.
        ModuleHandler spyHandler = spy(moduleHandler);
        doReturn(false).when(spyHandler).canRead(any(), any());
        doReturn(false).when(spyHandler).isExported(any(), any());

        // The classLoader should not be null
        assertNotNull(source.getClassLoader());
        assertNull(source.getClassLoader().getParent());
        doThrow(new RuntimeException()).when(byteBuddy).subclass(Mockito.<Class<?>>any());
        MockitoException exception = assertThrows(MockitoException.class,
            () -> spyHandler.adjustModuleGraph(source, target, true, true));
            assertTrue(exception.getMessage().contains("Could not create a carrier for making the Mockito type visible to"));
    }
}
