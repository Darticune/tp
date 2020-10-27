package commands;

import access.Access;
import manager.admin.Admin;
import manager.admin.ModuleList;
import manager.module.Module;
import storage.Storage;
import ui.Ui;

import static common.Messages.MODULE;

public class AddModuleCommand extends AddCommand {
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a new module. \n"
            + "Parameters:" + MODULE_PARAMETERS + "\n"
            + "Example: " + COMMAND_WORD + " CS2113T\n";


    private String module;

    public AddModuleCommand(String module) {
        this.module = module;
    }

    @Override
    public void execute(Ui ui, Access access, Storage storage) {
        Module module = new Module(this.module);
        String result = addModule(access, storage, module);
        ui.showToUser(result);
    }

    private String addModule(Access access, Storage storage, Module module) {
        Admin newAdmin = access.getAdmin();
        ModuleList modules = newAdmin.getModules();
        modules.addModule(module);
        int moduleCount = modules.getModuleCount();
        access.setAdmin(newAdmin);
        storage.createModule(module.getModuleName());
        return prepareResult(MODULE, module.toString(), moduleCount);
    }
}