//
// Replace the function console.log with this original function.
//
(function() {

  printLog = function() {
    var now = new Date().toISOString();
    var logLevel = arguments[0];
    var args = arguments[1];
    var origFunc = arguments[2];
    var logger = document.getElementById('debugLog');
    var prefix = '[' + now + '] ' + (logLevel ? logLevel + ': ' : '');
    for (var i = 0; i < args.length; i++) {
      logger.innerHTML += prefix + args[i] + '<br />';
    }
    origFunc.apply(undefined, args);
  };


  console.log_orig = console.log;
  console.log = function() { printLog('', arguments, console.log_orig); };

  console.info_orig = console.error;
  console.info = function() { printLog('INFO', arguments, console.info_orig); };

  console.warn_orig = console.error;
  console.warn = function() { printLog('WARN', arguments, console.warn_orig); };

  console.error_orig = console.error;
  console.error = function() { printLog('ERROR', arguments, console.error_orig); };

  console.log("Debug Log enabled.")
})();
