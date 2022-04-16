# server
Source code for image process server. This module handles communication between _mobile client_ and the image _classification model_.
  - Server and mobile client communicate through TCP/Socket protocol.
  - The _classification model_ is an independently running instance under Caffe framework. I modified Caffe source code a bit so that it could receive command line from _server_. However, this part is sadly lost after those years.

