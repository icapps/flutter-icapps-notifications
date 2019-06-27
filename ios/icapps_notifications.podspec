#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

require 'yaml'
pubspec = YAML.load_file(File.join('..', 'pubspec.yaml'))
libraryVersion = pubspec['version'].gsub('+', '-')

Pod::Spec.new do |s|
  s.name             = 'icapps_notifications'
  s.version          = '0.0.1'
  s.summary          = 'Firebase Cloud Messaging plugin for Flutter with the icapps standards.'
  s.description      = <<-DESC
Firebase Cloud Messaging plugin for Flutter.
                       DESC
  s.homepage         = 'https://github.com/icapps'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Koen Van Looveren' => 'koen.vanlooveren@icapps.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'Firebase/Core'
  s.dependency 'Firebase/Messaging'
  s.static_framework = true
  s.ios.deployment_target = '8.0'

  s.prepare_command = <<-CMD
      echo // Generated file, do not edit > Classes/UserAgent.h
      echo "#define LIBRARY_VERSION @\\"#{libraryVersion}\\"" >> Classes/UserAgent.h
      echo "#define LIBRARY_NAME @\\"flutter-fire-fcm\\"" >> Classes/UserAgent.h
    CMD
end
