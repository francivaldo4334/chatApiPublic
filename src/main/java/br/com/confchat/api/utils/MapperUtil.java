package br.com.confchat.api.utils;

import br.com.confchat.api.models.ContactModel;
import br.com.confchat.api.models.DeviceModel;
import br.com.confchat.api.models.MessageModel;
import br.com.confchat.api.viewmodel.ContactViewModel;
import br.com.confchat.api.viewmodel.DeviceViewModel;
import br.com.confchat.api.viewmodel.MessageViewModel;

import java.util.UUID;

public class MapperUtil
{
    public static DeviceViewModel mapToViewModel(DeviceModel data, UUID localDevice){
        var deviceViewModel = new DeviceViewModel();
        deviceViewModel.setId(data.getId());
        deviceViewModel.setName(data.getName());
        deviceViewModel.setThisDevice(localDevice.equals(data.getId()));
        return deviceViewModel;
    }
    public static ContactViewModel mapToViewModel(ContactModel data){
        var viewModel = new ContactViewModel();
        viewModel.setUserId(data.getUserContact().getId());
        viewModel.setName(data.getName());
        viewModel.setChatId(data.getFkChat().getId());
        return viewModel;
    }

    public static MessageViewModel mapToViewModel(MessageModel data) {
        var viewModel = new MessageViewModel();
        viewModel.setContent(data.getContent());
        viewModel.setCreateAt(data.getCreateAt());
        viewModel.setUserId(data.getUser().getId());
        viewModel.setRead(data.isRead());
        viewModel.setUserName(data.getUser().getName());
        return viewModel;
    }
}
