/*
 * Copyright (c) 2025 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.RandomString;

import java.sql.DriverManager;

public class ByteBuddyMockHandlerTest {
    @Test
    public void generated_mockito_module_probe_class_is_loaded() throws ClassNotFoundException{
        // Ensure the creation of a ModuleSystemFound.
        ByteBuddy byteBuddy = new ByteBuddy();
        SubclassLoader loader = mock(SubclassLoader.class);
        // Create instance using make.
        ModuleHandler moduleHandler = ModuleHandler.make(byteBuddy, loader);
        // Needed when byteBuddy is creating the class, avoids NullPointerException when using Mock SubclassLoader.
        when(loader.resolveStrategy(any(), any(), anyBoolean())).thenReturn(ClassLoadingStrategy.Default.INJECTION);
        // Ensure we get past the first if statement.
        ModuleHandler spyHandler = spy(moduleHandler);
        doReturn(false).when(spyHandler).canRead(any(), any());
        doReturn(false).when(spyHandler).isExported(any(), any());

        // Using real classes that are not loaded by the bootstrap.
        Class<?> source = this.getClass();
        Class<?> target = Mockito.class;

        // Should generate mock class.
        spyHandler.adjustModuleGraph(source, target, true, true);

        // Expected name of the generated class. The expected name is based on the code in adjustModuleGraph.
        String expectedClassName = String.format(
                "%s$%s$%s%s",
                source.getName(),
                "MockitoModuleProbe",
                RandomString.hashOf(source.getName().hashCode()),
                RandomString.hashOf(target.getName().hashCode()));

        // Should be able to load the generated class.
        Class<?> generatedClass = Class.forName(expectedClassName, true, source.getClassLoader());

        // The class should be null and have the expected name.
        assertNotNull(generatedClass);
        assertEquals(expectedClassName, generatedClass.getName());
    }
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
    public void throws_exception_if_carrier_creation_fails(){
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
        // Make bytebuddy fail when trying to create carrier.
        doThrow(new RuntimeException()).when(byteBuddy).subclass(Mockito.<Class<?>>any());
        MockitoException exception = assertThrows(MockitoException.class,
            () -> spyHandler.adjustModuleGraph(source, target, true, true));
            assertTrue(exception.getMessage().contains("Could not create a carrier for making the Mockito type visible to"));
    }

    @Test
    public void source_parent_classLoader_matches_but_is_bootstrap(){
        // Mock classes needed to create instance of ModuleSystemFound.
        ByteBuddy byteBuddy = mock(ByteBuddy.class);
        SubclassLoader loader = mock(SubclassLoader.class);
        ModuleHandler moduleHandler = ModuleHandler.make(byteBuddy, loader); // Should return an instance of ModuleSystemFound.

        Class<?> source = DriverManager.class; // source.GetParent().getClassLoader() will return null(bootstrap).
        Class<?> target = Integer.class; // source.getParent().getClassLoader() matches the classLoader of target.
        // Forcing conditions so that needsExport/needsRead are true.
        ModuleHandler spyHandler = spy(moduleHandler);
        doReturn(false).when(spyHandler).canRead(any(), any());
        doReturn(false).when(spyHandler).isExported(any(), any());

        // The classLoader should not be null
        assertNotNull(source.getClassLoader());
        assertNull(source.getClassLoader().getParent());
        // Because the parent classLoader of source is the bootstrap we cannot adjust that module.
        MockitoException exception = assertThrows(MockitoException.class,
            () -> spyHandler.adjustModuleGraph(source, target, true, true));
            assertTrue(exception.getMessage().contains("Could not force module adjustment of the module of "));
    }
}
