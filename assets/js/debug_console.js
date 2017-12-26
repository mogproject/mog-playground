//
// Replace the function console.log with this original function.
//
replaceConsoleLog = function() {
  console.old = console.log;

  console.log = function() {
    var logger = document.getElementById('debugLog');
    var prefix = '[' + new Date().toISOString() + '] ';
    for (var i = 0; i < arguments.length; i++) {
      logger.innerHTML += prefix + arguments[i] + '<br />';
    }
    console.old.apply(undefined, arguments);
  };
};