# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  
  config.vm.box = "hashicorp/precise32"
  config.vm.box_url = "http://files.vagrantup.com/precise32.box"
  config.vm.box_download_checksum_type = "sha256"
  config.vm.box_download_checksum = "e89aa739a5fd250221c273879b131765495d76f1a107f2c908b25888309881da"
  
  config.vm.provision :puppet do |puppet|
    puppet.manifests_path = '.puppet/manifests'
    puppet.manifest_file = 'default.pp'
    puppet.module_path = '.puppet/modules'
    puppet.options = '--verbose --debug'
  end

  # forward port 8000
  config.vm.network "forwarded_port", guest: 80, host: 8000

end
