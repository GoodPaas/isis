package org.isisaddons.module.excel.fixture.demoapp.demomodule.fixturescripts;

import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.extensions.fixtures.fixturescripts.FixtureScript;
import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.fixture.demoapp.demomodule.fixturehandlers.demotodoitem.DemoToDoItemRowHandler;
import org.isisaddons.module.excel.fixture.demoapp.todomodule.dom.ExcelDemoToDoItem;

import lombok.Getter;

import javax.inject.Inject;

public class DemoToDoItem_create_usingExcelFixture extends FixtureScript {

    private final String user;

    public DemoToDoItem_create_usingExcelFixture() {
        this(null);
    }

    public DemoToDoItem_create_usingExcelFixture(String ownedBy) {
        this.user = ownedBy;
    }


    @Getter
    private List<ExcelDemoToDoItem> todoItems = Lists.newArrayList();

    @Override
    public void execute(ExecutionContext executionContext) {

        final String ownedBy = this.user != null ? this.user : userService.getUser().getName();

        installFor(ownedBy, executionContext);

        transactionService.flushTransaction();
    }

    private void installFor(String user, ExecutionContext ec) {

        ec.setParameter("user", user);

        this.todoItems.addAll(load(ec, "ToDoItems.xlsx"));
        this.todoItems.addAll(load(ec, "MoreToDoItems.xlsx"));

        transactionService.flushTransaction();
    }

    private List<ExcelDemoToDoItem> load(
            final ExecutionContext executionContext,
            final String resourceName) {
        final URL excelResource = Resources.getResource(getClass(), resourceName);
        final ExcelFixture excelFixture = new ExcelFixture(excelResource, DemoToDoItemRowHandler.class);
        excelFixture.setExcelResourceName(resourceName);
        executionContext.executeChild(this, excelFixture);

        return (List<ExcelDemoToDoItem>) excelFixture.getObjects();
    }


    @Inject UserService userService;
    @Inject TransactionService transactionService;


}