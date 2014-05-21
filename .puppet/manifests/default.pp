# http://redminecrm.com/boards/4/topics/448-installing-redmine-2-5-passenger-nginx-rvm-on-ubuntu-12-04

class redmine {

  exec { 'system_update':
    command => 'apt-get update',
    path    => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"];
  }

  $sysPackages = [ "curl", "mysql-server", "libmysqlclient-dev", "git-core", "subversion", "imagemagick", "libmagickwand-dev", "libcurl4-openssl-dev" ]
  package { $sysPackages:
    ensure  => "installed",
    require => Exec['system_update']
  }

  exec { 'install_rvm':
    command => 'curl -L https://get.rvm.io | bash -s stable --ruby=2.0.0-p481',
    path    => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    creates => '/usr/local/rvm',
    require => Package['curl'];
  }
  
  exec { 'remove_rvm_profile':
    command => 'rm -f /etc/profile.d/rvm.sh',
    onlyif  => 'test -f /etc/profile.d/rvm.sh',
    path    => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => Exec['install_rvm'];
  }
  
  exec { 'install_passenger':
    command => 'rvm-shell ruby-2.0.0-p481 -c "gem install passenger -v 4.0.41"',
    unless  => 'rvm-shell ruby-2.0.0-p481 -c "gem list passenger -v 4.0.41 -i"',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => Exec['install_rvm'];
  }
  
  exec { 'install_nginx':
    command => 'rvm-shell ruby-2.0.0-p481 -c "passenger-install-nginx-module --auto --auto-download --prefix=/opt/nginx"',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    creates => "/opt/nginx",
    require => Exec['install_passenger'];
  }
  
  exec { 'download_nginx_init':
    command  => 'wget -q https://raw.githubusercontent.com/jnstq/rails-nginx-passenger-ubuntu/master/nginx/nginx -O /etc/init.d/nginx',
    path     => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    creates  => "/etc/init.d/nginx",
    require => Exec['install_nginx'];
  }
  
  file { "/etc/init.d/nginx":
    mode    => 0755,
    owner   => "root",
    group   => "root",
    require => Exec["download_nginx_init"];
  }
  
  file { "/var/data":
    ensure => "directory";
  }
  
  exec { 'checkout_redmine':
    command => "svn co http://svn.redmine.org/redmine/branches/2.5-stable redmine",
    cwd     => "/var/data",
    path    => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    creates => "/var/data/redmine",
    require => [File["/var/data"], Package["subversion"]];
  }
  
  exec { 'bundle_install':
    command => 'rvm-shell ruby-2.0.0-p481 -c "bundle install"',
    cwd     => '/var/data/redmine',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => [Exec['install_rvm'], Exec['checkout_redmine']];
  }
  
  file { '/var/data/redmine/config/database.yml':
    source => '/vagrant/.puppet/files/database.yml',
    require => Exec['checkout_redmine'];
  }
  
  file { '/var/data/redmine/public/plugin_assets':
    ensure  => "directory",
    owner   => "www-data",
    group   => "www-data",
    mode    => 0755,
    require => Exec['checkout_redmine'];
  }
  
  file { '/var/data/redmine/tmp':
    ensure  => "directory",
    owner   => "www-data",
    group   => "www-data",
    mode    => 0755,
    recurse => true,
    require => Exec['checkout_redmine'];
  }
  
  service { 'mysql':
    enable => true,
    ensure => running,
    require => Package["mysql-server"],
  }
  
  exec { 'mysql_set_admin_password':
    command => "mysqladmin -uroot password root",
    path    => ["/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    unless => "mysqladmin -uroot -proot status",
    require => Service["mysql"],
  }
  
  exec { "mysql_create_db":
    command => "/usr/bin/mysql -uroot -proot -e \"create database redmine; grant all on redmine.* to redmine@localhost identified by 'redmine';\"",
    unless => "/usr/bin/mysql -uredmine -predmine redmine",
    require => [Service["mysql"], Exec['mysql_set_admin_password']],
  }
  
  exec { 'rake_db_migrate':
    command => 'rvm-shell ruby-2.0.0-p481 -c "bundle exec rake db:migrate"',
    cwd     => '/var/data/redmine',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => [File['/var/data/redmine/config/database.yml'], Exec['checkout_redmine'], Exec['bundle_install'], Exec['mysql_create_db']];
  }
  
  exec { 'rake_redmine_plugins':
    command => 'rvm-shell ruby-2.0.0-p481 -c "bundle exec rake redmine:plugins"',
    cwd     => '/var/data/redmine',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => Exec['rake_db_migrate'];
  }
  
  exec { 'rake_secret_token':
    command => 'rvm-shell ruby-2.0.0-p481 -c "bundle exec rake generate_secret_token"',
    cwd     => '/var/data/redmine',
    path    => ["/usr/local/rvm/bin", "/bin/","/sbin/","/usr/bin/","/usr/sbin/"],
    require => Exec['rake_redmine_plugins'];
  }
  
  file { '/opt/nginx/conf/nginx.conf':
    source    => '/vagrant/.puppet/files/nginx.conf',
    owner     => 'root',
    group     => 'root',
    notify    => Service['nginx'],
    require   => Exec['install_nginx'];
  }
  
  service { 'nginx':
    enable => true,
    ensure => running,
    require => Exec['rake_secret_token'];
  }
}

include redmine