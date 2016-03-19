VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "hashicorp/precise32"
  config.vm.box_url = "http://files.vagrantup.com/precise32.box"
  config.vm.network "forwarded_port", guest: 8089, host: 8888
  config.vm.synced_folder "./", "/uwc9"
  config.vm.provision "shell", path: "provision.sh"
end
